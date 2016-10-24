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

import java.util.Locale;
  
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import spray.json._
import DefaultJsonProtocol._

import scala.concurrent.{ ExecutionContext, Promise }
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.joda.time.DateTime

//////////////////////////////////////////////////////////////////////////

case class Color(name: String, red: Int, green: Int, blue: Int)

case class Team(name: String, color: Option[Color])

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat(Color, "name", "r", "g", "b")
  implicit val teamFormat = jsonFormat(Team, "name", "jersey")
}

import MyJsonProtocol._

//////////////////////////////////////////////////////////////////////////

class WebSocketFrameHandler(pushActor: ActorRef)(implicit ec: ExecutionContext) extends SimpleChannelInboundHandler[WebSocketFrame] {

    val logger = LoggerFactory.getLogger(getClass)

    @throws(classOf[Exception])
    def channelRead0(ctx: ChannelHandlerContext, frame: WebSocketFrame) {

        if (frame.isInstanceOf[TextWebSocketFrame]) {
            val request: String = frame.asInstanceOf[TextWebSocketFrame].text();
            logger.info("frame: " + frame + ", request: " + request)
        } else {
            val message: String = "unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
    }

    @throws(classOf[Exception])
    def messageReceived(ctx: ChannelHandlerContext, message: WebSocketFrame) {
       
        logger.info("message: " + message);

        if (message.isInstanceOf[TextWebSocketFrame]) {

            pushActor ! WebSocketRegistered("kalle", ctx)

            val request: String = message.asInstanceOf[TextWebSocketFrame].text();
   
            logger.info("message: " + message + ", request: " + request)    

            val obj = Team("Red Sox", Some(Color("Red", 255, 0, 0)))
            val ast = obj.toJson

            logger.info("Current timestamp: " + DateTime.now())

            //ctx.channel().writeAndFlush(new TextWebSocketFrame(ast.compactPrint));

        } else {

             // BinaryWebSocketFrame, CloseWebSocketFrame, ContinuationWebSocketFrame, PingWebSocketFrame, PongWebSocketFrame, TextWebSocketFrame
             logger.info("OTHER: " + message)
        }
    }
}
