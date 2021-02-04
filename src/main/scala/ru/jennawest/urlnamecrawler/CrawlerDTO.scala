package ru.jennawest.urlnamecrawler

object CrawlerDTO {

  case class Request(urls: Seq[String])

  sealed trait UrlResponse
  case class SuccessUrlResponse(url: String, name: String) extends UrlResponse
  case class FailedUrlResponse(url: String, error: String) extends UrlResponse

  case class FullResponse(urls: Seq[UrlResponse])

  sealed trait CrawlerError extends Throwable
  case class FailedPageResponse(httpCode: Int, message: String) extends CrawlerError {
    override def getMessage: String = message
  }

  object TitleNotFound extends CrawlerError {
    override def getMessage: String = "Title not found"
  }


}
