package ru.jennawest.urlnamecrawler

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model._
import HttpMethods._
import akka.stream.Materializer
import akka.util.ByteString
import org.jsoup.Jsoup
import com.typesafe.scalalogging.Logger
import ru.jennawest.urlnamecrawler.CrawlerDTO._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait CrawlerService {

  def crawlNames(urls: Seq[String])(implicit as: ActorSystem, ec: ExecutionContext): Future[FullResponse]

}

class CrawlerServiceImpl(defaultProtocol: String, userAgent: String) extends CrawlerService {

  private val log = Logger(getClass)

  override def crawlNames(urls: Seq[String])(implicit as: ActorSystem, ec: ExecutionContext): Future[FullResponse] = {
    val aggregatedUrls = getMainPagesUrls(urls)

    val namesResult = getNamesForUrlMap(aggregatedUrls.urlsWithMainPage)
    val result = prepareUrlsMap(namesResult)
    val failedRes = prepareFailedUrls(aggregatedUrls.failedUrls)

    result.map { mainResult =>
      FullResponse(mainResult ++ failedRes)
    }
  }

  def getMainPageUrl(url: String): Try[Uri] = {
    val fixedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
      defaultProtocol + "://" + url
    } else {
      url
    }

    Try(Uri(fixedUrl)).map { u =>
      val scheme = if (u.scheme.isEmpty) {
        defaultProtocol
      } else {
        u.scheme
      }
      Uri.from(scheme = scheme).withAuthority(host = u.authority.host, port = u.authority.port)
    }
  }

  def transformPageName(pageName: Future[String], urls: Seq[String])(implicit ec: ExecutionContext): Future[Seq[UrlResponse]] =
    pageName.transform {
      case Success(name) =>
        Success(urls.map { u =>
          SuccessUrlResponse(u, name)
        })
      case Failure(ex) =>
        Success(urls.map { u =>
          FailedUrlResponse(u, ex.getMessage)
        })
    }

  def requestContent(uri: Uri)(implicit as: ActorSystem, ec: ExecutionContext): Future[String] =
    Http().singleRequest(HttpRequest(uri = uri, headers = Seq(headers.`User-Agent`(userAgent)))).flatMap { resp =>
      log.debug("For url {} got response status {}", uri, resp.status)
      log.debug("headers: {}", resp.headers)
      if (resp.status.isSuccess()) {
        getContentString(resp)
      } else {
        getContentString(resp).transform {
          case Success(value) => Failure[String](FailedPageResponse(resp.status.intValue(), value))
          case Failure(t) =>
            val err = FailedPageResponse(resp.status.intValue(), "Could not parse error message: " + t.getMessage)
            Failure[String](err)
        }
      }
    }

  def getNameFromHtml(html: String): Try[String] = Try {
    Jsoup.parse(html).title()
  }.flatMap { n =>
    if (n.isEmpty) {
      Failure(TitleNotFound)
    } else {
      Success(n)
    }
  }

  private def prepareFailedUrls(urls: Seq[String]): Seq[UrlResponse] =
    urls.map(u => FailedUrlResponse(u, "Could not extract main page url"))

  private def getNamesForUrlMap(urlsMap: Map[Uri, Seq[String]])(implicit as: ActorSystem, ec: ExecutionContext): Seq[Future[Seq[UrlResponse]]] =
    urlsMap.map { entity =>
      val mainPageUrl = entity._1
      val pageName = requestContent(mainPageUrl).flatMap(c => Future.fromTry(getNameFromHtml(c)))
      transformPageName(pageName, entity._2)
    }.toSeq

  private def prepareUrlsMap(urlResponses: Seq[Future[Seq[UrlResponse]]])(implicit ec: ExecutionContext): Future[Seq[UrlResponse]] =
    Future.sequence(urlResponses).map(_.flatten)

  private def getMainPagesUrls(urls: Seq[String]): AggregatedUrls =
    urls.foldLeft(AggregatedUrls.empty) { (acc, elem) =>
      getMainPageUrl(elem) match {                   // собираем урлы главных страниц сайтов;
        case Success(mainPageUrl) =>                 // возможна ситуация, когда в запросе есть урлы нескольких страниц одного и того же сайта
          log.debug("Got main page url {} for url: {}", mainPageUrl, elem)
          val mapValue = acc.urlsWithMainPage        // поэтому агрегируем их
            .get(mainPageUrl)
            .map(_ :+ elem)
            .getOrElse(Seq(elem))

          acc.copy(urlsWithMainPage = acc.urlsWithMainPage + (mainPageUrl -> mapValue))

        case Failure(ex) =>
          log.debug("Cannot get main page for url {}; error: {}", elem, ex.getMessage)
          acc.copy(failedUrls = acc.failedUrls :+ elem)  // отдельно сохраняем список урлов,
                                                         // для которых не получилось добыть урл главной страницы
      }
    }

  private def getContentString(resp: HttpResponse)(implicit ec: ExecutionContext, mat: Materializer): Future[String] =
    resp.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map(_.utf8String)

}

case class AggregatedUrls(urlsWithMainPage: Map[Uri, Seq[String]], failedUrls: Seq[String])

object AggregatedUrls {
  def empty: AggregatedUrls = AggregatedUrls(Map.empty, Seq.empty)
}

