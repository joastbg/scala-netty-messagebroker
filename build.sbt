organization := "com.gecemmo"

version := "0.1"

scalaVersion := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "netty all" at "https://mvnrepository.com/artifact/io.netty/",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "The New Motion Public Repo" at "http://nexus.thenewmotion.com/content/groups/public/"
)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-actors" % "2.11.8",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.11",  
  "io.netty" % "netty-all" % "5.0.0.Alpha2",
  "io.netty" % "netty-tcnative" % "1.1.33.Fork23",
  "io.netty" % "netty-example" % "5.0.0.Alpha2",
  "net.debasishg" % "redisclient" % "3.2" from "http://localhost/redisclient_2.11-3.2.jar",
  "com.thenewmotion.akka" %% "akka-rabbitmq" % "2.3",
  "io.spray" % "spray-json_2.11" % "1.3.2",
  "joda-time" % "joda-time" % "2.9.4",
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "org.slf4j" % "slf4j-simple" % "1.7.21",
  "com.jcabi" % "jcabi-log" % "0.17.2",
  "com.typesafe.scala-logging" % "scala-logging_2.11" % "3.5.0",
  "com.typesafe.scala-logging" % "scala-logging-slf4j_2.11" % "2.1.2",
  "commons-codec" % "commons-codec" % "1.10"
)

parallelExecution in Test := false
