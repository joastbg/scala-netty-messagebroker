
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

case class Push[A](topic: String, payload: A)
case class WebSocketRegistered(topic: String, ctx: ChannelHandlerContext)

class WebSocketPushActor extends Actor with Logger {
    //val logg = Logging(context.system, this)
    val groups: ConcurrentMap[String, Option[ChannelHandlerContext]] = new ConcurrentHashMap[String, Option[ChannelHandlerContext]]
    /**
    * Process incoming messages
    */
    def receive = {

         case Push(topic, payload) => {

          println("**** WebSocketPushActor :: received Push :: " + topic + ", " + payload)         
          //groups.get(topic) match {
          //  case Some(c) => c.write(new TextWebSocketFrame(payload.toString())) 
          //  case _ => log.warn("No topic found for: " + topic)

            print(groups.get(topic))

            //groups.get(topic).map(x => x.writeText("tjena mittmena"))

            groups.get(topic) match {
                case Some(ctx) => ctx.channel().writeAndFlush(new TextWebSocketFrame(payload.toString()));
                case _ => println("No topic found for: " + topic)
              }

          }
         

        case WebSocketRegistered(topic, channel) =>  {        

                println("**** WebSocketPushActor :: received WebSocketRegistered :: " + topic + ", " + channel)         

                // TODO: dont do putIfAbsent every time
                //groups.put(topic, channel)
                groups.putIfAbsent(topic, Some(channel))
                //groups.get(topic).map(_ add(channel))
                groups.get(topic) match {
                //case Some(c) => c.writeAndFlush(new TextWebSocketFrame("push notification")) 
                case _ => println("No topic found for: " + topic)
              }

              //groups.get(topic).map(_ add(channel))  

        }
        case _ => {
          log.info("WebSocketPushActor :: received unknown")         
        }
    }

}
