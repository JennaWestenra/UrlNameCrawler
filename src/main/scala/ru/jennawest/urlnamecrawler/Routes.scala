package ru.jennawest.urlnamecrawler

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.akkahttp.SwaggerAkka

import scala.concurrent.ExecutionContext

class Routes(service: CrawlerService)(implicit as: ActorSystem, ec: ExecutionContext) {

  def routes: Route = getUrlNamesRoute ~ docsRoute

  def getUrlNamesRoute: Route =
    Endpoints.toRoute(Endpoints.getUrlNames)(input => service.crawlNames(input.urls).map(r => Right(r)))

  def docsRoute: Route = {
    val docs = OpenAPIDocsInterpreter.toOpenAPI(Endpoints.allEndpoints, "Url name crawler", "v1")
    new SwaggerAkka(docs.toYaml).routes
  }

}
