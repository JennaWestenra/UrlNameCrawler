package ru.jennawest.urlnamecrawler

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import com.typesafe.scalalogging.Logger
import ru.jennawest.urlnamecrawler.domain.dtos._
import ru.jennawest.urlnamecrawler.helpers._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait CrawlerService {

  def crawlNames(urls: Seq[String])(implicit as: ActorSystem, ec: ExecutionContext): Future[FullResponse]

}

class CrawlerServiceImpl(urlHelper: UrlHelper, httpHelper: HttpHelper, htmlHelper: HtmlHelper) extends CrawlerService {

  private val log = Logger(getClass)

  /**
   * Tries to get main page url for site;
   * for sites that have main page, requests names and transforms all urls to UrlResponse
   *
   *
   * @param urls to crawl its names
   * @return FullResponse with response (good or bad) for every url
   */
  override def crawlNames(urls: Seq[String])(implicit as: ActorSystem, ec: ExecutionContext): Future[FullResponse] = {
    val aggregatedUrls = getMainPagesUrls(urls)

    val namesResult = getNamesForUrlMap(aggregatedUrls.urlsWithMainPage)
    val result = prepareUrlsMap(namesResult)
    val failedRes = prepareFailedUrls(aggregatedUrls.failedUrls)

    result.map { mainResult =>
      FullResponse(mainResult ++ failedRes)
    }
  }

  /**
   * Transforms future with pageName to successful future of sequence of UrlResponses.
   * It prepares all futures to lift with Future.sequence,
   * so that if there is one failed future, we still can get the result for all other urls.
   *
   * @param pageName future with requested pageName for urls
   * @param urls  urls that have pageName as name
   * @return Future with sequence of a prepared UrlResponses (may be FailedUrlResponse or SuccessUrlResponse)
   */
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

  /**
   * Extracts main page for urls and aggregates them by main page.
   * Also collects urls that don't have main page extracted.
   *
   * @param urls urls to partition
   * @return AggregatedUrls
   */
  private def getMainPagesUrls(urls: Seq[String]): AggregatedUrls =
    urls.foldLeft(AggregatedUrls.empty) { (acc, elem) =>
      urlHelper.getMainPageUri(elem) match {
        case Success(mainPageUrl) =>
          log.debug("Got main page url {} for url: {}", mainPageUrl, elem)
          val mapValue = acc.urlsWithMainPage
            .get(mainPageUrl)
            .map(_ :+ elem)
            .getOrElse(Seq(elem))

          acc.copy(urlsWithMainPage = acc.urlsWithMainPage + (mainPageUrl -> mapValue))

        case Failure(ex) =>
          log.debug("Cannot get main page for url {}; error: {}", elem, ex.getMessage)
          acc.copy(failedUrls = acc.failedUrls :+ elem)

      }
    }

  /**
   * Transforms urls that have no main page extracted into FailedUrlResponse
   *
   */
  private def prepareFailedUrls(urls: Seq[String]): Seq[UrlResponse] =
    urls.map(u => FailedUrlResponse(u, "Could not extract main page url"))

  /**
   * Requests page content for very key in map and tries to get page name from it
   *
   * @param urlsMap map with main page uri key and connected urls
   */
  private def getNamesForUrlMap(urlsMap: Map[Uri, Seq[String]])(implicit as: ActorSystem, ec: ExecutionContext): Seq[Future[Seq[UrlResponse]]] =
    urlsMap.map { entity =>
      val mainPageUrl = entity._1
      val pageName = httpHelper.requestContent(mainPageUrl).flatMap(c => Future.fromTry(htmlHelper.getNameFromHtml(c)))
      transformPageName(pageName, entity._2)
    }.toSeq

  private def prepareUrlsMap(urlResponses: Seq[Future[Seq[UrlResponse]]])(implicit ec: ExecutionContext): Future[Seq[UrlResponse]] =
    Future.sequence(urlResponses).map(_.flatten)

}

case class AggregatedUrls(urlsWithMainPage: Map[Uri, Seq[String]], failedUrls: Seq[String])

object AggregatedUrls {
  def empty: AggregatedUrls = AggregatedUrls(Map.empty, Seq.empty)
}

