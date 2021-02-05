import sbt._

object Dependencies {

  val scalaVersion = "2.13.4"

  val pureConfigVersion     = "0.14.0"
  val typesafeConfigVersion = "1.4.1"

  val akkaVersion          = "2.6.12"
  val akkaHttpVersion      = "10.2.3"
  val jsoupVersion         = "1.13.1"
  val akkaHttpCirceVersion = "1.35.3"
  val circeVersion         = "0.13.0"
  val tapirVersion         = "0.17.9"

  val scalaLoggingVersion = "3.9.2"
  val logbackVersion      = "1.2.3"

  val scalatestVersion  = "3.2.3"
  val scalacheckVersion = "1.15.2"

  val pureConfig     = "com.github.pureconfig" %% "pureconfig" % pureConfigVersion
  val typesafeConfig = "com.typesafe"          % "config"      % typesafeConfigVersion

  val akkaActors    = "com.typesafe.akka" %% "akka-actor"      % akkaVersion
  val akkaStream    = "com.typesafe.akka" %% "akka-stream"     % akkaVersion
  val akkaHttp      = "com.typesafe.akka" %% "akka-http"       % akkaHttpVersion
  val jsoup         = "org.jsoup"         % "jsoup"            % jsoupVersion
  val akkaHttpCirce = "de.heikoseeberger" %% "akka-http-circe" % akkaHttpCirceVersion
  val circe         = "io.circe"          %% "circe-core"      % circeVersion
  val circeGeneric  = "io.circe"          %% "circe-generic"   % circeVersion

  val tapirAkkaHttpServer =
    ("com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % tapirVersion).exclude("com.typesafe.akka", "akka-stream_2.12")
  val tapirAkkaHttpSwagger =
    ("com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-akka-http" % tapirVersion).exclude("com.typesafe.akka", "akka-stream_2.12")
  val tapirCore       = "com.softwaremill.sttp.tapir" %% "tapir-core"               % tapirVersion
  val tapirDocs       = "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % tapirVersion
  val tapirJsonCirce  = "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % tapirVersion
  val tapirYamlCirce  = "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion

  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging"  % scalaLoggingVersion
  val logback      = "ch.qos.logback"             % "logback-classic" % logbackVersion

  val scalatest  = "org.scalatest"  %% "scalatest"  % scalatestVersion  % Test
  val scalacheck = "org.scalacheck" %% "scalacheck" % scalacheckVersion % Test

  val config = Seq(pureConfig, typesafeConfig)
  val tapir  = Seq(tapirAkkaHttpServer, tapirAkkaHttpSwagger, tapirCore, tapirDocs, tapirJsonCirce, tapirYamlCirce)
  val akka   = Seq(akkaActors, akkaStream, akkaHttp)

  val dependencies: Seq[ModuleID] =
    Seq(
      jsoup,
      akkaHttpCirce,
      circe,
      circeGeneric,
      scalaLogging,
      logback
    ) ++ config ++ akka ++ tapir

  val testDependencies: Seq[ModuleID] = Seq(scalatest, scalacheck)

}
