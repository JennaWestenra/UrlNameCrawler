package ru.jennawest.urlnamecrawler.helpers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri, headers}
import akka.stream.Materializer
import akka.util.ByteString
import com.typesafe.scalalogging.Logger
import ru.jennawest.urlnamecrawler.AppConfig
import ru.jennawest.urlnamecrawler.domain.errors.FailedPageResponse

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait HttpHelper {

  /*
  * Requests content for given Uri
  */
  def requestContent(uri: Uri)(implicit as: ActorSystem, ec: ExecutionContext): Future[String]

}

object HttpHelper extends HttpHelper {

  private val log = Logger(getClass)

  override def requestContent(uri: Uri)(implicit as: ActorSystem, ec: ExecutionContext): Future[String] = {
    Http().singleRequest(HttpRequest(uri = uri, headers = Seq(headers.`User-Agent`(AppConfig.cfg.userAgent)))).flatMap { resp =>
      log.debug("For url {} got response status {}", uri, resp.status)
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
  }

  private def getContentString(resp: HttpResponse)(implicit ec: ExecutionContext, mat: Materializer): Future[String] =
    resp.entity.dataBytes.runFold(ByteString(""))(_ ++ _).map(_.utf8String)

}

