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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spray.json._
import DefaultJsonProtocol._

import scala.concurrent.{ ExecutionContext, Promise }

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import scala.concurrent.duration._

import org.joda.time.DateTime

//////////////////////////////////////////////////////////////////////////////////////////////////
//case class Subrecord(value: String)
//case class Record(a: String, b: String, subrecord: Subrecord)

case class Color(name: String, red: Int, green: Int, blue: Int)

case class Team(name: String, color: Option[Color])

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat(Color, "name", "r", "g", "b")
  implicit val teamFormat = jsonFormat(Team, "name", "jersey")
}
import MyJsonProtocol._

/*
case class Answer(code: Int, content: String)

object MasterJsonProtocol extends JsonFormat {
  implicit val anwserFormat = jsonFormat2(Answer)
}*/

/*
object RecordFormat extends JsonFormat[Record] {
    def write(obj: Record): JsValue = {
      JsObject(
        ("a", JsString(obj.a)),
        ("b", JsString(obj.b)),
        ("reason", JsString(obj.subrecord.value))
      )
    }

    def read(json: JsValue): Record = json match {
      case JsObject(fields)
            if fields.isDefinedAt("a") & fields.isDefinedAt("b") & fields.isDefinedAt("reason") =>
              Record(fields("a").convertTo[String],
                fields("b").convertTo[String],
                Subrecord(fields("reason").convertTo[String])
              )

          case _ => deserializationError("Not a Record")
    }
}*/
//////////////////////////////////////////////////////////////////////////////////////////////////


class WebSocketFrameHandler(pushActor: ActorRef)(implicit ec: ExecutionContext) extends SimpleChannelInboundHandler[WebSocketFrame] {

    @throws(classOf[Exception])
    def channelRead0(ctx: ChannelHandlerContext, frame: WebSocketFrame) {

        println(" **** WebSocketFrameHandler::channelRead0")

        if (frame.isInstanceOf[TextWebSocketFrame]) {
              // Send the uppercase string back.
              val request: String = frame.asInstanceOf[TextWebSocketFrame].text();
              //logger.info("{} received {}", ctx.channel(), request);
              println(">>>> " + frame)    
              println(">>>> " + request)
    
              //tx.channel().writeAndFlush(new TextWebSocketFrame("hello world"));

          } else {
              val message: String = "unsupported frame type: " + frame.getClass().getName();
              throw new UnsupportedOperationException(message);
          }
    }

    @throws(classOf[Exception])
    def messageReceived(ctx: ChannelHandlerContext, message: io.netty.handler.codec.http.websocketx.WebSocketFrame) {
        println(">>>> " + message);

        if (message.isInstanceOf[TextWebSocketFrame]) {

            pushActor ! WebSocketRegistered("kalle", ctx)

            // Send the uppercase string back.
            val request: String = message.asInstanceOf[TextWebSocketFrame].text();
            //logger.info("{} received {}", ctx.channel(), request);
            println(">>>> " + message)    
            println(">>>> " + request)

            //val record = Answer(1, "Tjena Johan")
            //val recordToJson = record.toJson
            //val recordFromJson = recordToJson.convertTo[Record]

            //println(recordToJson)
            //assert(recordFromJson == record)

            val obj = Team("Red Sox", Some(Color("Red", 255, 0, 0)))
            val ast = obj.toJson

            println("===> " + DateTime.now())

            ctx.channel().writeAndFlush(new TextWebSocketFrame(ast.prettyPrint));
        }else {
              println(" ---------> " + message)
          }
    }
}