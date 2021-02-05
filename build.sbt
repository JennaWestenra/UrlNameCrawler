
val commonSettings = Seq(
  scalaVersion := Dependencies.scalaVersion,
  scalacOptions ++= List(
    "-unchecked",
    "-deprecation",
    "-encoding",
    "UTF8",
    "-feature",
    "-target:jvm-1.8"
  ),
  resolvers ++= Seq(
    Resolver.mavenLocal,
    Resolver.mavenCentral,
    Resolver.typesafeRepo("releases")
  )
)

val buildSettings = Seq(
  assemblyJarName := "url-name-crawler.jar",
  mainClass := Some("ru.jennawest.urlnamecrawler.Main")
)

lazy val `url-crawler` = project
  .in(file("."))
  .settings(commonSettings ++ buildSettings)
  .settings(
    organization := "ru.jennawest",
    name := "url-name-crawler",
    libraryDependencies ++= Dependencies.dependencies ++ Dependencies.testDependencies
  )
  .enablePlugins(JavaAppPackaging)