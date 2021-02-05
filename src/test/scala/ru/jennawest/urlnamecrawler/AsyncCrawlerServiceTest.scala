package ru.jennawest.urlnamecrawler

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import org.scalatest.{BeforeAndAfterAll, TryValues}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import ru.jennawest.urlnamecrawler.domain.dtos._
import ru.jennawest.urlnamecrawler.domain.errors._
import ru.jennawest.urlnamecrawler.helpers._

import scala.concurrent.{ExecutionContext, Future}

class AsyncCrawlerServiceTest extends AsyncWordSpec with Matchers with BeforeAndAfterAll with TryValues {

  implicit private val as: ActorSystem = ActorSystem("name-crawler-service")
  import as.dispatcher

  val title   = "my title"
  val service = new CrawlerServiceImpl(UrlHelper, new HttpHelperMock(title), HtmlHelper)

  "Crawler service" must {

    "transform success future pagename to SuccessUrlResponse" in {
      val pageName = Future.successful("vk")
      val result   = service.transformPageName(pageName, List("https://vk.com"))

      result.map { r =>
        r shouldBe Seq(SuccessUrlResponse("https://vk.com", "vk"))
      }
    }

    "transform success future pagename to a Seq of SuccessUrlResponse" in {
      val pageName = Future.successful("vk")
      val result   = service.transformPageName(pageName, List("https://vk.com", "vk.com", "vk.com/feed"))

      result.map { r =>
        r shouldBe Seq(
          SuccessUrlResponse("https://vk.com", "vk"),
          SuccessUrlResponse("vk.com", "vk"),
          SuccessUrlResponse("vk.com/feed", "vk")
        )
      }
    }

    "transform failed future pagename to FailedUrlResponse" in {
      val pageName = Future.failed(FailedPageResponse(503, "Server error"))
      val result   = service.transformPageName(pageName, List("https://vk.com"))

      result.map { r =>
        r shouldBe Seq(FailedUrlResponse("https://vk.com", "Server error"))
      }
    }

    "transform failed future pagename to a seq of FailedUrlResponse" in {
      val pageName = Future.failed(FailedPageResponse(503, "Server error"))
      val result   = service.transformPageName(pageName, List("https://vk.com", "vk.com", "vk.com/feed"))

      result.map { r =>
        r shouldBe Seq(
          FailedUrlResponse("https://vk.com", "Server error"),
          FailedUrlResponse("vk.com", "Server error"),
          FailedUrlResponse("vk.com/feed", "Server error")
        )
      }
    }

    "crawl title from one successful page" in {
      val result = service.crawlNames(List("https://vk.com", "vk.com", "vk.com/feed"))

      result.map { r =>
        r shouldBe FullResponse(
          Seq(
            SuccessUrlResponse("https://vk.com", title),
            SuccessUrlResponse("vk.com", title),
            SuccessUrlResponse("vk.com/feed", title)
          )
        )
      }
    }

    "crawl title from three successful page" in {
      val result = service.crawlNames(List("https://vk.com", "instagram.com", "https://youtube.com/asfdv?sddvcsdffgxc&dfcvfbdb"))

      result.map { r =>
        r shouldBe FullResponse(
          Seq(
            SuccessUrlResponse("https://vk.com", title),
            SuccessUrlResponse("instagram.com", title),
            SuccessUrlResponse("https://youtube.com/asfdv?sddvcsdffgxc&dfcvfbdb", title)
          )
        )
      }
    }

  }

}

class HttpHelperMock(title: String) extends HttpHelper {

  override def requestContent(uri: Uri)(implicit as: ActorSystem, ec: ExecutionContext): Future[String] =
    Future.successful(s"<html><head><title>$title</title></head></html>")

}
