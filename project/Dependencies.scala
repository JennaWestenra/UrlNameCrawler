import sbt._

// akkahttp
// jsoup
// circe
// scalatest scalacheck
// scalalogging logback
//
object Dependencies {

  val scalaVersion = "2.13.4"

  val pureConfigVersion     = "0.14.0"
  val typesafeConfigVersion = "1.4.1"

  val akkaHttpVersion      = "10.2.3"
  val jsoupVersion         = "1.13.1"
  val akkaHttpCirceVersion = "1.35.3"
  val tapirVersion         = "0.17.9"

  val scalatestVersion  = "3.2.3"
  val scalacheckVersion = "1.15.2"

  val pureConfig     = "com.github.pureconfig" %% "pureconfig" % pureConfigVersion
  val typesafeConfig = "com.typesafe"          % "config"      % typesafeConfigVersion

  val akkaHttp      = "com.typesafe.akka"           %% "akka-http"       % akkaHttpVersion
  val jsoup         = "org.jsoup"                   % "jsoup"            % jsoupVersion
  val akkaHttpCirce = "de.heikoseeberger"           %% "akka-http-circe" % akkaHttpCirceVersion
  val tapir         = "com.softwaremill.sttp.tapir" %% "tapir-core"      % tapirVersion

  val scalatest  = "org.scalatest"  %% "scalatest"  % scalatestVersion  % Test
  val scalacheck = "org.scalacheck" %% "scalacheck" % scalacheckVersion % Test

  val dependencies     = Seq(pureConfig, typesafeConfig, akkaHttp, jsoup, akkaHttpCirce, tapir)
  val testDependencies = Seq(scalatest, scalacheck)

}
