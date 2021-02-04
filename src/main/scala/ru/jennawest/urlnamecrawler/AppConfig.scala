package ru.jennawest.urlnamecrawler

import pureconfig._
import pureconfig.generic.auto._

case class AppConfig(
  host: String,
  port: Int,
  defaultProtocol: String,
  userAgent: String
)

object AppConfig {
  val cfg: AppConfig = ConfigSource.default.loadOrThrow[AppConfig]
}
