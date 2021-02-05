package ru.jennawest.urlnamecrawler

import akka.http.scaladsl.model.Uri
import org.scalatest.TryValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import ru.jennawest.urlnamecrawler.helpers.UrlHelper

class UrlHelperTest extends AnyWordSpec with Matchers with TryValues {

  "UrlHelper" must {

    "return valid main page for https://vk.com/feed" in {
      val uri = UrlHelper.getMainPageUri("https://vk.com/feed")
      uri.success.value shouldBe Uri.from(scheme = "https", host = "vk.com")
    }

    "return valid main page for vk.com/feed" in {
      val uri = UrlHelper.getMainPageUri("vk.com/feed")
      uri.success.value shouldBe Uri.from(scheme = "https", host = "vk.com")
    }

    "return valid main page for https://vk.com" in {
      val uri = UrlHelper.getMainPageUri("https://vk.com")
      uri.success.value shouldBe Uri.from(scheme = "https", host = "vk.com")
    }

    "return valid main page for vk.com" in {
      val uri = UrlHelper.getMainPageUri("vk.com")
      uri.success.value shouldBe Uri.from(scheme = "https", host = "vk.com")
    }

    "return failure for invalid characters" in {
      val uri = UrlHelper.getMainPageUri("ыvk.com")
      uri.isFailure shouldBe true
    }
  }
}
