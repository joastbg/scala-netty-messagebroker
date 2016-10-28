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

import org.apache.commons.codec.binary.Base64;

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//case class Payload(data: String, size: Int)
//case class Message(dest: String, payload: Option[Payload])

case class Company (name: String, orgnr: String)
case class Request (company: Company, timestamp: Long, title: String, status: Int, activity: List[String])

object MyJsonProtocol1 extends DefaultJsonProtocol {
  implicit val companyFormat = jsonFormat2(Company.apply)
  implicit val requestFormat = jsonFormat5(Request.apply)  
}

import MyJsonProtocol1._

//---------------

object MyJsonProtocol3 extends DefaultJsonProtocol {
  implicit val payloadFormat = jsonFormat(Payload, "data", "size")
  implicit val messageFormat = jsonFormat(Message, "dest", "payload")  
}

import MyJsonProtocol3._

case class NamedList[A](name: String, items: List[A])
case class Item(name: String, company: String)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit def namedListFormat[A :JsonFormat] = jsonFormat2(NamedList.apply[A])  
  implicit val ItemDTO = jsonFormat2(Item.apply)
}

import MyJsonProtocol._

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

            val request: String = message.asInstanceOf[TextWebSocketFrame].text();
            
            request match {
                case "register" => {
                    pushActor ! WebSocketRegistered("kalle", ctx)
                    logger.info(" >> ws client register :: " + message)
                }

                case "rpc" => {
                    val nl = NamedList[Item](name = "leads", items = Item("Moran Kaufman", "MK Publishers Inc.")::Item("Sam Anderson",      "Enron  Inc.")::Item("Mason Freeman", "Motorola Inc.")::Item("Tony Montana", "American Tobacco Inc.")::Nil)
                    val na = nl.toJson.compactPrint
                    val msg = Base64.encodeBase64String(na.toString().getBytes())
                    val obj = Message("rpc", Some(Payload(msg, msg.length())))
                    val ast = obj.toJson
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(ast.compactPrint))
                }

                case "reqs" => {
                    val company01 = Company("Gautr Systems AB", "556922-9486")
                    val req = Request(company01, (new java.util.Date).getTime, "I want to buy red carpets", 0, List("One", "Two", "Three"))
                    val ra = req.toJson.compactPrint
                    val msg = Base64.encodeBase64String(ra.toString().getBytes())
                    val obj = Message("rpc", Some(Payload(msg, msg.length())))
                    val ast = obj.toJson
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(ast.compactPrint))
                }

                case _ => logger.info(" >> ws client UNHANDLED :: " + message)
            }

            logger.info("Current timestamp: " + DateTime.now())

        } else {

             // BinaryWebSocketFrame, CloseWebSocketFrame, ContinuationWebSocketFrame, 
             // PingWebSocketFrame, PongWebSocketFrame, TextWebSocketFrame

             logger.info("OTHER: " + message)
        }
    }
}
