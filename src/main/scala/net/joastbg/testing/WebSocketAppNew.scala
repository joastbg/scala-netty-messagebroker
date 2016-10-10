/*

    Raptor Toolkit, Copyright (C) 2013-2016, Gautr Systems AB

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package net.joastbg.testing

import org.mashupbots.socko.events.HttpResponseStatus
import org.mashupbots.socko.routes._
import org.mashupbots.socko.infrastructure.Logger
import org.mashupbots.socko.webserver.WebServer
import org.mashupbots.socko.webserver.WebServerConfig

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
//import io.netty.handler.ssl.SslContext;
//import io.netty.handler.ssl.SslContextBuilder;
//import io.netty.handler.ssl.util.SelfSignedCertificate;

object WebSocketAppNew extends Logger {

  val actorSystem = ActorSystem("WebSocketeActorSystem")

  def main(args: Array[String]) {
   
    val bossGroup = new NioEventLoopGroup()
    val workerGroup = new NioEventLoopGroup()

    val b = new ServerBootstrap()

    val pushActor = actorSystem.actorOf(Props[WebSocketPushActor])

    b.group(bossGroup, workerGroup)
     .channel(classOf[NioServerSocketChannel])
     //.localAddress(new InetSocketAddress(port))
     .handler(new LoggingHandler(LogLevel.INFO))
     .childHandler(new WebSocketServerInitializer(actorSystem, pushActor))

    val ch: Channel = b.bind(8888).sync().channel()

    //actorSystem.actorOf(pushActor ! Push("kalle", "From scheduler")
    //actorSystem.scheduler.scheduleOnce(10 seconds, 0 minutes, pushActor, Push("kalle", "From scheduler"))

    val mq = new PublishSubscriber(pushActor)

    ch.closeFuture().sync()

    System.out.println("Goto: http://localhost:8888/html")
  }
}
