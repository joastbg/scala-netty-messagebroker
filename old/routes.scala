package net.joastbg.testing

import akka.actor.ActorRef

import org.mashupbots.socko.events.HttpResponseStatus
import org.mashupbots.socko.routes._

import java.io.BufferedInputStream
import java.io.FileInputStream

trait Routes {

  def webSocketPushActor:ActorRef

  val routes = Routes({
    case HttpRequest(httpRequest) => httpRequest match {

      case GET(Path("/html")) => {
        // Send 100 continue if required
        if (httpRequest.request.is100ContinueExpected) {
          httpRequest.response.write100Continue()
        }
        // Load HTML-file for WebSocket
        val buf = scala.io.Source.fromFile("wwwroot/ws.html").mkString
        httpRequest.response.write(buf.toString, "text/html; charset=UTF-8")
      }

      case GET(Path("/jquery.gritter.css")) => {
        // Send 100 continue if required
        if (httpRequest.request.is100ContinueExpected) {
          httpRequest.response.write100Continue()
        }
        // Load CSS-file for WebSocket
        val buf = scala.io.Source.fromFile("wwwroot/jquery.gritter.css").mkString
        httpRequest.response.write(buf.toString, "text/css; charset=UTF-8")
      }

      case GET(Path("/jquery.gritter.js")) => {
        // Send 100 continue if required
        if (httpRequest.request.is100ContinueExpected) {
          httpRequest.response.write100Continue()
        }
        // Load CSS-file for WebSocket
        val buf = scala.io.Source.fromFile("wwwroot/jquery.gritter.js").mkString
        httpRequest.response.write(buf.toString, "text/javascript; charset=UTF-8")
      }

      case GET(Path("/images/gritter.png")) => {
        // Send 100 continue if required
        if (httpRequest.request.is100ContinueExpected) {
          httpRequest.response.write100Continue()
        }
        // Load CSS-file for WebSocket
        //val buf = scala.io.Source.fromFile("wwwroot/gritter.png").mkString

        val bis = new BufferedInputStream(new FileInputStream("wwwroot/gritter.png"))
        val bArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray

        httpRequest.response.write(bArray, "image/png")
      }

      case GET(Path("/images/ie-spacer.gif")) => {
        // Send 100 continue if required
        if (httpRequest.request.is100ContinueExpected) {
          httpRequest.response.write100Continue()
        }
        // Load CSS-file for WebSocket
        //val buf = scala.io.Source.fromFile("wwwroot/gritter.png").mkString

        val bis = new BufferedInputStream(new FileInputStream("wwwroot/ie-spacer.gif"))
        val bArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray

        httpRequest.response.write(bArray, "image/gif")
      }
      
      case PathSegments("send" :: what :: Nil) => {
        // Send event to all web sockets
        webSocketPushActor ! Push[String](what, "Johan rocks! " + what)
        httpRequest.response.write("message sent", "text/html; charset=UTF-8")
      }
      
      case _ => httpRequest.response.write(HttpResponseStatus.NOT_FOUND)
    }
    
    case WebSocketHandshake(wsHandshake) => wsHandshake match {

      case PathSegments(path :: Nil) => {
        wsHandshake.authorize()
        webSocketPushActor ! WebSocketRegistered(path, wsHandshake.context.channel)
      }
    }
    case WebSocketFrame(wsFrame) => { /* Implement */ }
  })
}
