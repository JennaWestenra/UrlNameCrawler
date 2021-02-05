package ru.jennawest.urlnamecrawler.domain

object dtos {

  case class Request(urls: Seq[String])

  sealed trait UrlResponse

  case class SuccessUrlResponse(url: String, name: String) extends UrlResponse

  case class FailedUrlResponse(url: String, error: String) extends UrlResponse

  case class FullResponse(urls: Seq[UrlResponse])

}
