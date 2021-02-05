package ru.jennawest.urlnamecrawler.helpers

import akka.http.scaladsl.model.Uri
import ru.jennawest.urlnamecrawler.AppConfig

import scala.util.Try

trait UrlHelper {

  /**
   * Returns main page Uri for given url
   */
  def getMainPageUri(url: String): Try[Uri]

}

object UrlHelper extends UrlHelper {

  override def getMainPageUri(url: String): Try[Uri] = {
    val fixedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
      AppConfig.cfg.defaultProtocol + "://" + url
    } else {
      url
    }

    Try(Uri(fixedUrl)).map { u =>
      val scheme = if (u.scheme.isEmpty) {
        AppConfig.cfg.defaultProtocol
      } else {
        u.scheme
      }
      Uri.from(scheme = scheme).withAuthority(host = u.authority.host, port = u.authority.port)
    }
  }

}
