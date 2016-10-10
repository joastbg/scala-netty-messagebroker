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

        case WebSocketRegistered(topic, ctx) =>  {        

            logger.debug(topic + ", " + ctx)
            if (ctx.channel().isOpen()) {
                groups.put(topic, Some(ctx))
            } else {
                // ...
            }

            groups.get(topic) match {
                case _ => logger.debug("No topic found for: " + topic)
            }
        }

        case _ => {            
            logger.debug("Received unknown message")         
        }
    }
}
