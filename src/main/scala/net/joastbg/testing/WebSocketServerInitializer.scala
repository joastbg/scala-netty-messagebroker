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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.timeout.IdleStateHandler
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent
import io.netty.handler.timeout.IdleState
import io.netty.buffer.Unpooled

import scala.concurrent.{ ExecutionContext, Promise }


import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import scala.concurrent.duration._

class MyHandler(pushActor: ActorRef)(implicit ec: ExecutionContext) extends ChannelDuplexHandler {
    
     @throws(classOf[Exception])
     override def userEventTriggered(ctx: ChannelHandlerContext, evt: Object) {

        println(" ------------> userEventTriggered: " + evt)
     
        if (evt.isInstanceOf[IdleStateEvent]) {        
              val e:IdleStateEvent = evt.asInstanceOf[IdleStateEvent];

            

        println(" ------------> userEventTriggered :: IdleStateEvent: " + e)
        //pushActor ! Push("kalle", "From scheduler")
        ctx.channel().writeAndFlush(new PingWebSocketFrame(Unpooled.copiedBuffer("Raptor Ping".getBytes())));

            

             if (e.state() == IdleState.READER_IDLE) {
                 ctx.close();
             } else if (e.state() == IdleState.WRITER_IDLE) {
                 //ctx.writeAndFlush(new PingMessage());
                     //ctx.channel().writeAndFlush(new TextWebSocketFrame("hello world"));


                    
             }
         }
     }
 }

class WebSocketServerInitializer(actorSystem: ActorSystem, pushActor: ActorRef)(implicit ec: ExecutionContext) extends ChannelInitializer[SocketChannel] {

    val WEBSOCKET_PATH = "/websocket"
    val DEFAULT_CONNECT_TIMEOUT = 30

    @throws(classOf[Exception])
    override def initChannel(ch: SocketChannel) {

        println(" **** WebSocketServerInitializer::initChannel: " + ch + " >> " + ch.localAddress())

        //var pipeline:ChannelPipeline = ch.pipeline();
        ch.pipeline().addLast(
                    new HttpRequestDecoder(),
                    new HttpObjectAggregator(65536),
                    new HttpResponseEncoder(),
                    new WebSocketServerProtocolHandler("/websocket"),
                    new WebSocketFrameHandler(pushActor));


         ch.pipeline.addFirst("idleStateHandler", new IdleStateHandler(0, 0, DEFAULT_CONNECT_TIMEOUT));
          //ch.pipeline.addAfter("idleStateHandler", "idleEventHandler", timeoutHandler);
         ch.pipeline().addLast("myHandler", new MyHandler(pushActor));

         //channel.pipeline().addLast("myHandler", new MyHandler());

//        actorSystem.actorOf(Props[net.joastbg.testing.WebSocketPushActor]) ! Push("kalle", "From scheduler")

        
/*
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
        pipeline.addLast(new WebSocketIndexPageHandler(WEBSOCKET_PATH));
        pipeline.addLast(new WebSocketFrameHandler());
*/
    }

}