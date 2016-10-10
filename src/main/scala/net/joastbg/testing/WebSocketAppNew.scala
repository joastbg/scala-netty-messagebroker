package net.joastbg.testing

import org.mashupbots.socko.events.HttpResponseStatus
import org.mashupbots.socko.routes._
import org.mashupbots.socko.infrastructure.Logger
import org.mashupbots.socko.webserver.WebServer
import org.mashupbots.socko.webserver.WebServerConfig

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

import akka.actor.ActorSystem
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
   
    val bossGroup = new NioEventLoopGroup();
    val workerGroup = new NioEventLoopGroup();

    val b = new ServerBootstrap();

    b.group(bossGroup, workerGroup)
     .channel(classOf[NioServerSocketChannel])
     //.localAddress(new InetSocketAddress(port))
     .handler(new LoggingHandler(LogLevel.INFO))
     .childHandler(new WebSocketServerInitializer());

    val ch: Channel = b.bind(8888).sync().channel();

    ch.closeFuture().sync();

    System.out.println("Goto: http://localhost:8888/html")
  }
}
