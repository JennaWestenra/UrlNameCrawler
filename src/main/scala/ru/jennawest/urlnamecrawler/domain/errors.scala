package ru.jennawest.urlnamecrawler.domain

object errors {

  sealed trait CrawlerError extends Throwable
  case class FailedPageResponse(httpCode: Int, message: String) extends CrawlerError {
    override def getMessage: String = message
  }

  object TitleNotFound extends CrawlerError {
    override def getMessage: String = "Title not found"
  }

}
