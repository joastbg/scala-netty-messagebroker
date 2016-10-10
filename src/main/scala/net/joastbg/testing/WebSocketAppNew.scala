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

object WebSocketAppNew {

  val actorSystem = ActorSystem("WebSocketeActorSystem")

  def main(args: Array[String]) {
   
    val bossGroup = new NioEventLoopGroup()
    val workerGroup = new NioEventLoopGroup()

    val b = new ServerBootstrap()

    val pushActor = actorSystem.actorOf(Props[WebSocketPushActor])

    b.group(bossGroup, workerGroup)
     .channel(classOf[NioServerSocketChannel])
     .handler(new LoggingHandler(LogLevel.INFO))
     .childHandler(new WebSocketServerInitializer(actorSystem, pushActor))

    val ch: Channel = b.bind(8888).sync().channel()
    val mq = new PublishSubscriber(pushActor)

    ch.closeFuture().sync()

    System.out.println("Goto: http://localhost:8888/html")
  }
}
