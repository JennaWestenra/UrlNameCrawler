package ru.jennawest.urlnamecrawler

import akka.actor.ActorSystem
import org.scalatest.{BeforeAndAfterAll, TryValues}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import ru.jennawest.urlnamecrawler.CrawlerDTO._

import scala.concurrent.Future

class AsyncCrawlerServiceTest extends AsyncWordSpec with Matchers with BeforeAndAfterAll with TryValues {

  implicit private val as: ActorSystem = ActorSystem("name-crawler-service")
  import as.dispatcher

  val service = new CrawlerServiceImpl("https")

  "Crawler service" must {

    "transform success future pagename to SuccessUrlResponse" in {
      val pageName = Future.successful("vk")
      val result = service.transformPageName(pageName, List("https://vk.com"))

      result.map { r =>
        r shouldBe Seq(SuccessUrlResponse("https://vk.com", "vk"))
      }
    }

    "transform success future pagename to a Seq of SuccessUrlResponse" in {
      val pageName = Future.successful("vk")
      val result = service.transformPageName(pageName, List("https://vk.com", "vk.com", "vk.com/feed"))

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
      val result = service.transformPageName(pageName, List("https://vk.com"))

      result.map { r =>
        r shouldBe Seq(FailedUrlResponse("https://vk.com", "Server error"))
      }
    }

    "transform failed future pagename to a seq of FailedUrlResponse" in {
      val pageName = Future.failed(FailedPageResponse(503, "Server error"))
      val result = service.transformPageName(pageName, List("https://vk.com", "vk.com", "vk.com/feed"))

      result.map { r =>
        r shouldBe Seq(
          FailedUrlResponse("https://vk.com", "Server error"),
          FailedUrlResponse("vk.com", "Server error"),
          FailedUrlResponse("vk.com/feed", "Server error")
        )
      }
    }

  }

}
