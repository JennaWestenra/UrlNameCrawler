name := "UrlNameCrawler"

version := "0.0.1"

scalaVersion := Dependencies.scalaVersion
scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Dependencies.dependencies ++ Dependencies.testDependencies