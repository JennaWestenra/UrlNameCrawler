package ru.jennawest.urlnamecrawler

import akka.actor.ActorSystem
import akka.http.scaladsl.Http

object Main extends App{

  implicit val as: ActorSystem = ActorSystem("crawler-actor-system")
  import as.dispatcher

  val service = new CrawlerServiceImpl(AppConfig.cfg.defaultProtocol)
  val routes = new Routes(service)

  Http().newServerAt(AppConfig.cfg.host, AppConfig.cfg.port).bindFlow(routes.getUrlNamesRoute)

}
