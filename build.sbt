import play.PlayJava

name := "sproutup"

scalaVersion := "2.11.2"

version := "1.0-SNAPSHOT"

val appDependencies = Seq(
  "be.objectify"  %% "deadbolt-java"     % "2.3.2",
  // Comment the next line for local development of the Play Authentication core:
  //"com.feth"      %% "play-authenticate" % "0.6.8",
  "mysql" % "mysql-connector-java" % "5.1.18",
  javaCore,
  cache,
  javaWs,
  javaJdbc,
  javaEbean,
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "bootstrap" % "3.2.0",
  "com.amazonaws" % "aws-java-sdk" % "1.9.17",
  "joda-time" % "joda-time" % "2.7",
  "com.typesafe.play.plugins" %% "play-plugins-redis" % "2.3.1"
)

libraryDependencies += "com.github.tototoshi" %% "play-flyway" % "1.2.0"

libraryDependencies += "net.coobird" % "thumbnailator" % "0.4.8"

libraryDependencies += "org.scribe" % "scribe" % "1.3.6"

libraryDependencies += "org.xerial.snappy" % "snappy-java" % "1.1.1.7"

libraryDependencies += "com.sendgrid" % "sendgrid-java" % "2.2.1"

libraryDependencies += "com.google.http-client" % "google-http-client" % "1.20.0"

libraryDependencies += "com.google.http-client" % "google-http-client-jackson2" % "1.20.0"

libraryDependencies += "com.google.oauth-client" % "google-oauth-client" % "1.20.0"

libraryDependencies += "com.google.api-client" % "google-api-client" % "1.20.0"

libraryDependencies += "com.google.apis" % "google-api-services-urlshortener" % "v1-rev41-1.20.0"

libraryDependencies += "com.google.api-client" % "google-api-client-appengine" % "1.20.0"

libraryDependencies += "org.jsoup" % "jsoup" % "1.8.2"

resolvers += "org.sedis" at "http://pk11-scratch.googlecode.com/svn/trunk"

//  Uncomment the next line for local development of the Play Authenticate core:
lazy val playAuthenticate = project.in(file("modules/play-authenticate")).enablePlugins(PlayJava)

lazy val root = project.in(file("."))
  .enablePlugins(PlayJava)
  //.enablePlugins(SbtWeb)
  /* Uncomment the next lines for local development of the Play Authenticate core: */
  .dependsOn(playAuthenticate)
  .aggregate(playAuthenticate)
  .settings(
    libraryDependencies ++= appDependencies
  )

//JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

//pipelineStages := Seq(rjs)

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"
