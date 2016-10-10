organization := "com.gecemmo"

version := "0.1"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "The New Motion Public Repo" at "http://nexus.thenewmotion.com/content/groups/public/"
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-actors" % "2.11.8",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.11",
  "org.mashupbots.socko" % "socko-webserver_2.11" % "0.6.0",
  "io.netty" % "netty-all" % "5.0.0.Alpha2",
  "net.debasishg" % "redisclient" % "3.2" from "http://localhost/redisclient_2.11-3.2.jar",
  "com.thenewmotion.akka" %% "akka-rabbitmq" % "2.3"
)

parallelExecution in Test := false
