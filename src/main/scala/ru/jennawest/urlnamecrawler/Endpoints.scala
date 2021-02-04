package ru.jennawest.urlnamecrawler

import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import ru.jennawest.urlnamecrawler.CrawlerDTO._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

object Endpoints extends AkkaHttpServerInterpreter with JsonCodec {

  val getUrlNames: Endpoint[Request, String, FullResponse, Any] =
    endpoint
      .get
      .in("urls" / "names")
      .in(jsonBody[Request])
      .errorOut(stringBody)
      .out(jsonBody[FullResponse])

}
