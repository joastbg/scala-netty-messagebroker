
package net.joastbg.testing

import java.text.SimpleDateFormat
import java.util.GregorianCalendar

import akka.actor.Actor
import akka.event.Logging

import akka.actor.ActorSystem
import akka.actor.Props

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.Channel
import io.netty.util.concurrent.GlobalEventExecutor
import java.util.concurrent.{ConcurrentMap, ConcurrentHashMap}

import io.netty.channel.ChannelHandlerContext;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;


class BroadcastActor extends Actor {
       
    def receive = {
      case _ => {
          println("WebSocketPushActor :: received unknown")         
        }
    }
}
