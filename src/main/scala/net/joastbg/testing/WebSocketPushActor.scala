
package net.joastbg.testing

import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.util.concurrent.{ConcurrentMap, ConcurrentHashMap}

import akka.actor.Actor
import akka.event.Logging
import akka.actor.ActorSystem
import akka.actor.Props

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor

import org.slf4j.Logger
import org.slf4j.LoggerFactory

//////////////////////////////////////////////////////////////////////////

case class Push[A](topic: String, payload: A)
case class WebSocketRegistered(topic: String, ctx: ChannelHandlerContext)

//////////////////////////////////////////////////////////////////////////

class WebSocketPushActor extends Actor {

    val groups: ConcurrentMap[String, Option[ChannelHandlerContext]] = new ConcurrentHashMap[String, Option[ChannelHandlerContext]]
    val logger = LoggerFactory.getLogger(getClass)

    def receive = {

         case Push(topic, payload) => {

            logger.debug(topic + ", " + payload)         

            groups.get(topic) match {
                case Some(ctx) => ctx.channel().writeAndFlush(new TextWebSocketFrame(payload.toString()));
                case _ => logger.debug("No topic found for: " + topic)
              }

          }
         

        case WebSocketRegistered(topic, channel) =>  {        

                logger.debug(topic + ", " + channel)         
                groups.putIfAbsent(topic, Some(channel))

                groups.get(topic) match {

                case _ => logger.debug("No topic found for: " + topic)
              }

        }
        case _ => {
          logger.debug("Received unknown message")         
        }
    }

}
