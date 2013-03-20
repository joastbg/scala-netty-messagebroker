package net.joastbg.testing

import akka.actor.{ActorSystem, Props}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

import org.mashupbots.socko.routes._
import org.mashupbots.socko.infrastructure.Logger
import org.mashupbots.socko.webserver.WebServer
import org.mashupbots.socko.webserver.WebServerConfig

object WebSocketApp extends App with Logger with Routes {

  val actorSystem = ActorSystem("ActorSystem")

  val webSocketPushActor = actorSystem.actorOf(Props[WebSocketPushActor], "WebSocketPushActor")

  val webServer = new WebServer(WebServerConfig(), routes, actorSystem)

  // Stop webserver when app terminates
  Runtime.getRuntime.addShutdownHook(new Thread {
    override def run { webServer.stop() }
  })

  // Set up to push messages on fixed interval (testing)
  actorSystem.scheduler.schedule(0 seconds, 1 minutes, webSocketPushActor, Push("notifications", "From scheduler"))

  webServer.start()
}
