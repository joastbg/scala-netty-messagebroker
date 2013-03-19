package net.joastbg.testing

import akka.actor.ActorRef

import org.mashupbots.socko.events.HttpResponseStatus
import org.mashupbots.socko.routes._

trait Routes {

  def webSocket:ActorRef

  val routes = Routes({
    case HttpRequest(httpRequest) => httpRequest match {

      case GET(Path("/html")) => {
        // Send 100 continue if required
        if (httpRequest.request.is100ContinueExpected) {
        httpRequest.response.write100Continue()
        }

        // Load HTML-file for WebSocket
        val buf = scala.io.Source.fromFile("ws.html").mkString
        httpRequest.response.write(buf.toString, "text/html; charset=UTF-8")        
      }

      case Path("/favicon.ico") => {
        // If favicon.ico, just return a 404 because we don't have that file
        httpRequest.response.write(HttpResponseStatus.NOT_FOUND)
      }
      
      case PathSegments("send" :: what :: Nil) => {
        // Send event to all web sockets
        webSocket ! Push[String](what, "Johan rocks! " + what)
        httpRequest.response.write("message sent", "text/html; charset=UTF-8")
      }
      
      case _ => httpRequest.response.write(HttpResponseStatus.NOT_FOUND)
    }
    
    case WebSocketHandshake(wsHandshake) => wsHandshake match {

      case PathSegments(path :: Nil) => {
        wsHandshake.authorize()
        webSocket ! WebSocketRegistered(path, wsHandshake.channel)
      }
    }
    case WebSocketFrame(wsFrame) => { /* Implement */ }
  })
}