
package net.joastbg.testing

import java.text.SimpleDateFormat
import java.util.GregorianCalendar

import org.mashupbots.socko.events.HttpRequestEvent
import org.mashupbots.socko.events.WebSocketFrameEvent

import akka.actor.Actor
import akka.event.Logging

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._

import org.mashupbots.socko.infrastructure.Logger

import io.netty.channel.group.DefaultChannelGroup
import io.netty.channel.Channel
import io.netty.util.concurrent.GlobalEventExecutor
import java.util.concurrent.{ConcurrentMap, ConcurrentHashMap}

case class Push[A](topic: String, payload: A)
case class WebSocketRegistered(topic: String, channel: Channel)

class WebSocketPushActor extends Actor with Logger {
    //val logg = Logging(context.system, this)
val groups: ConcurrentMap[String, Option[DefaultChannelGroup]] = new ConcurrentHashMap[String, Option[DefaultChannelGroup]]
    /**
    * Process incoming messages
    */
    def receive = {

         case Push(topic, payload) => {


          log.info("**** WebSocketPushActor :: received Push :: " + topic + ", " + payload)         
          //groups.get(topic) match {
          //  case Some(c) => c.write(new TextWebSocketFrame(payload.toString())) 
          //  case _ => log.warn("No topic found for: " + topic)

            print(groups.get(topic))

            //groups.get(topic).map(x => x.writeText("tjena mittmena"))

            groups.get(topic) match {
                case Some(c) => c.write(payload.toString()) 
                case _ => log.warn("No topic found for: " + topic)
              }

          }
         

        case WebSocketRegistered(topic, channel) =>  {        

            log.info("**** WebSocketPushActor :: received WebSocketRegistered :: " + topic + ", " + channel)         

              // TODO: dont do putIfAbsent every time
              //groups.put(topic, channel)
      groups.putIfAbsent(topic, Some(new DefaultChannelGroup(GlobalEventExecutor.INSTANCE)))
      groups.get(topic).map(_ add(channel))
groups.get(topic) match {
                case Some(c) => c.write("hello world") 
                case _ => log.warn("No topic found for: " + topic)
              }

              //groups.get(topic).map(_ add(channel))  

        }
        case _ => {
          log.info("WebSocketPushActor :: received unknown")         
        }
    }

}
