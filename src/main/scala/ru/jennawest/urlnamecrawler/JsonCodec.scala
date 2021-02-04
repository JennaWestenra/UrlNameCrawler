package ru.jennawest.urlnamecrawler

import cats.syntax.functor._
import io.circe.{Codec, Decoder, Encoder}
import io.circe.generic.auto._
import io.circe.generic.semiauto._
import io.circe.syntax._

import ru.jennawest.urlnamecrawler.CrawlerDTO._

trait JsonCodec {

  implicit val requestCodec: Codec[Request]           = deriveCodec
  implicit val fullResponseCodec: Codec[FullResponse] = deriveCodec

  implicit val encodeUrlResponse: Encoder[UrlResponse] = Encoder.instance {
    case success: SuccessUrlResponse => success.asJson
    case failure: FailedUrlResponse  => failure.asJson
  }

  implicit val decodeUrlResponse: Decoder[UrlResponse] =
    List[Decoder[UrlResponse]](
      Decoder[SuccessUrlResponse].widen,
      Decoder[FailedUrlResponse].widen
    ).reduceLeft(_.or(_))

}
