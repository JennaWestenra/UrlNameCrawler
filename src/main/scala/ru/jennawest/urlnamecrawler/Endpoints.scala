package ru.jennawest.urlnamecrawler

import ru.jennawest.urlnamecrawler.domain.dtos._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

object Endpoints extends AkkaHttpServerInterpreter with JsonCodec {

  val getUrlNames: Endpoint[Request, String, FullResponse, Any] =
    endpoint
      .get
      .in("urls" / "names")
      .in(jsonBody[Request])
      .errorOut(stringBody)
      .out(jsonBody[FullResponse])

  val allEndpoints: Seq[Endpoint[_, _, _, _]] = Seq(getUrlNames)

}
