package ru.jennawest.urlnamecrawler

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.scalalogging.Logger

object Main extends App {

  private val log = Logger(getClass)

  implicit val as: ActorSystem = ActorSystem("crawler-actor-system")
  import as.dispatcher

  val service = new CrawlerServiceImpl(AppConfig.cfg.defaultProtocol, AppConfig.cfg.userAgent)
  val routes = new Routes(service)

  Http().newServerAt(AppConfig.cfg.host, AppConfig.cfg.port)
    .bindFlow(routes.getUrlNamesRoute).onComplete { b =>
    log.debug("Server started: {}", b)
  }

}
