package ru.jennawest.urlnamecrawler

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.scalalogging.Logger
import ru.jennawest.urlnamecrawler.helpers._

object Main extends App {

  private val log = Logger(getClass)

  implicit val as: ActorSystem = ActorSystem("crawler-actor-system")
  import as.dispatcher

  val service = new CrawlerServiceImpl(UrlHelper, HttpHelper, HtmlHelper)
  val routes  = new Routes(service)

  Http().newServerAt(AppConfig.cfg.host, AppConfig.cfg.port).bindFlow(routes.routes).foreach { b =>
    log.info("Server started: {}", b)
  }

}
