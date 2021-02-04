package ru.jennawest.urlnamecrawler

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri
import org.scalatest.{BeforeAndAfterAll, TryValues}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CrawlerServiceTest extends AnyWordSpec with Matchers with BeforeAndAfterAll with TryValues {

  implicit private val as: ActorSystem = ActorSystem("name-crawler-service")
  import as.dispatcher

  val title = "I am a site title!"
  val service = new CrawlerServiceImpl("https")

  "Crawler service" must {

    "return valid main page for https://vk.com/feed" in {
      val uri = service.getMainPageUrl("https://vk.com/feed")
      uri.success.value shouldBe (Uri.from(scheme = "https", host = "vk.com"))
    }

    "return valid main page for vk.com/feed" in {
      val uri = service.getMainPageUrl("vk.com/feed")
      uri.success.value shouldBe (Uri.from(scheme = "https", host = "vk.com"))
    }

    "return valid main page for https://vk.com" in {
      val uri = service.getMainPageUrl("https://vk.com")
      uri.success.value shouldBe (Uri.from(scheme = "https", host = "vk.com"))
    }

    "return valid main page for vk.com" in {
      val uri = service.getMainPageUrl("vk.com")
      uri.success.value shouldBe Uri.from(scheme = "https", host = "vk.com")
    }

    "return failure for invalid characters" in {
      val uri = service.getMainPageUrl("ыvk.com")
      uri.isFailure shouldBe true
    }

    "return content of tag title from html" in {
      val result = service.getNameFromHtml(s"<html><head><title>$title</title> and no title</head> again no title</html>")
      result.success.value shouldBe title
    }

    "return a failure if there is no title tag" in {
      val result = service.getNameFromHtml("<html><head> and no title</head> again no title</html>")
      println(result)
      result.isFailure shouldBe true
    }

    "return a failure if there is invalid html document" in {
      val result = service.getNameFromHtml(s"<htle>$title</title> and no /html>")
      println(result)
      result.isFailure shouldBe true
    }

    "return a failure if there is an empty title" in {
      val result = service.getNameFromHtml(s"<htle>$title</title> and no /html>")
      println(result)
      result.isFailure shouldBe true
    }

  }

  override def afterAll(): Unit = {
    super.afterAll()
    as.terminate()
  }

}
