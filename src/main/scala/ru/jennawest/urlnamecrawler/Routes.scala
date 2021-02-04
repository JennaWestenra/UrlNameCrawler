package ru.jennawest.urlnamecrawler

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route

import scala.concurrent.ExecutionContext

class Routes(service: CrawlerService)(implicit as: ActorSystem, ec: ExecutionContext) {

  def getUrlNamesRoute: Route =
    Endpoints.toRoute(Endpoints.getUrlNames)(input => service.crawlNames(input.urls).map(r => Right(r)))

}
