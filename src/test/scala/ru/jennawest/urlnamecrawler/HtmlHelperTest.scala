package ru.jennawest.urlnamecrawler

import org.scalatest.TryValues
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import ru.jennawest.urlnamecrawler.helpers.HtmlHelper

class HtmlHelperTest extends AnyWordSpec with Matchers with TryValues {

  val title = "I am a site title!"
  
  "HtmlHelper" must {
    
    "return content of tag title from html" in {
      val result = HtmlHelper.getNameFromHtml(s"<html><head><title>$title</title> and no title</head> again no title</html>")
      result.success.value shouldBe title
    }

    "return a failure if there is no title tag" in {
      val result = HtmlHelper.getNameFromHtml("<html><head> and no title</head> again no title</html>")
      println(result)
      result.isFailure shouldBe true
    }

    "return a failure if there is invalid html document" in {
      val result = HtmlHelper.getNameFromHtml(s"<htle>$title</title> and no /html>")
      println(result)
      result.isFailure shouldBe true
    }

    "return a failure if there is an empty title" in {
      val result = HtmlHelper.getNameFromHtml(s"<htle>$title</title> and no /html>")
      println(result)
      result.isFailure shouldBe true
    }
    
  }

}
