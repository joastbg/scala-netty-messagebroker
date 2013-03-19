organization := "com.gecemmo"

version := "0.1"

scalaVersion := "2.10.0"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka"    % "akka-actor_2.10"      % "2.1.0",
  "com.typesafe.akka"    % "akka-testkit_2.10"    % "2.1.0",
  "com.typesafe.akka"    % "akka-remote_2.10"     % "2.1.0",
  "org.mashupbots.socko" % "socko-webserver_2.10" % "0.2.4"
)

parallelExecution in Test := false