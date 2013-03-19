package net.joastbg.testing

import akka.actor.Actor
import org.mashupbots.socko.infrastructure.Logger

import org.jboss.netty.channel.Channel
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame
import org.jboss.netty.channel.group.DefaultChannelGroup

import java.util.concurrent.{ConcurrentMap, ConcurrentHashMap}

case class WebSocketRegistered(topic: String, channel: Channel)
case class Push[A](topic: String, payload: A)

class WebSocketPushActor extends Actor with Logger {
  
  // Channel groups mapped to topic
  val groups: ConcurrentMap[String, Option[DefaultChannelGroup]] = new ConcurrentHashMap[String, Option[DefaultChannelGroup]]

  def receive = {
    
    case Push(topic, payload) =>      
      groups.get(topic) match {
        case Some(c) => c.write(new TextWebSocketFrame(payload.toString())) 
        case _ => log.warn("No topic found for: " + topic)
      }

    case WebSocketRegistered(topic, channel) =>            
      // TODO: dont do putIfAbsent every time
      groups.putIfAbsent(topic, Some(new DefaultChannelGroup()))
      groups.get(topic).map(_ add(channel))
  }
}