
package net.joastbg.testing

import java.text.SimpleDateFormat
import java.util.GregorianCalendar

import org.mashupbots.socko.events.HttpRequestEvent
import org.mashupbots.socko.events.WebSocketFrameEvent

import akka.actor.Actor
import akka.event.Logging

import akka.actor.ActorSystem
import akka.actor.Props

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

import org.mashupbots.socko.infrastructure.Logger

import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.Channel
import io.netty.util.concurrent.GlobalEventExecutor
import java.util.concurrent.{ConcurrentMap, ConcurrentHashMap}

import io.netty.channel.ChannelHandlerContext;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;


class BroadcastActor extends Actor with Logger {
       
    def receive = {
      case _ => {
          log.info("WebSocketPushActor :: received unknown")         
        }
    }
}
