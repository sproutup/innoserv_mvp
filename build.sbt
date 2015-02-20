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
  "joda-time" % "joda-time" % "2.7"
)

libraryDependencies += "com.github.tototoshi" %% "play-flyway" % "1.2.0"

//  Uncomment the next line for local development of the Play Authenticate core:
lazy val playAuthenticate = project.in(file("modules/play-authenticate")).enablePlugins(PlayJava)

lazy val root = project.in(file("."))
  .enablePlugins(PlayJava)
  /* Uncomment the next lines for local development of the Play Authenticate core: */
  .dependsOn(playAuthenticate)
  .aggregate(playAuthenticate)
  .settings(
    libraryDependencies ++= appDependencies
  )

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"
