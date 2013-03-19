package net.joastbg.testing

import akka.actor.{ActorSystem, Props}

import org.mashupbots.socko.routes._
import org.mashupbots.socko.infrastructure.Logger
import org.mashupbots.socko.webserver.WebServer
import org.mashupbots.socko.webserver.WebServerConfig

object WebSocketApp extends App with Logger with Routes {

  val actorSystem = ActorSystem("ActorSystem")

  val webSocket = actorSystem.actorOf(Props[WebSocketPushActor], "WebSocketPushActor")

  val webServer = new WebServer(WebServerConfig(), routes, actorSystem)

  // Stop webserver when app terminates
  Runtime.getRuntime.addShutdownHook(new Thread {
    override def run { webServer.stop() }
  })

  webServer.start()
}
