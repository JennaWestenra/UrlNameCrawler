package ru.jennawest.urlnamecrawler.helpers

import org.jsoup.Jsoup
import ru.jennawest.urlnamecrawler.domain.errors.TitleNotFound

import scala.util.{Failure, Success, Try}

trait HtmlHelper {

  /*
  * Extracts title as pageName from html string
  */
  def getNameFromHtml(html: String): Try[String]

}

object HtmlHelper extends HtmlHelper {

  override def getNameFromHtml(html: String): Try[String] =
    Try {
      Jsoup.parse(html).title()
    }.flatMap { n =>
      if (n.isEmpty) {
        Failure(TitleNotFound)
      } else {
        Success(n)
      }
    }

}
