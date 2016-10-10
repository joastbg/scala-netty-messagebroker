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

import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleStateHandler
import io.netty.handler.timeout.IdleStateEvent
import io.netty.handler.timeout.IdleState
import io.netty.buffer.Unpooled

import scala.concurrent.{ ExecutionContext, Promise }
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MyHandler(pushActor: ActorRef)(implicit ec: ExecutionContext) extends ChannelHandlerAdapter {

    val logger = LoggerFactory.getLogger(getClass)    

    @throws(classOf[Exception])
    override def userEventTriggered(ctx: ChannelHandlerContext, evt: Object) {

        logger.debug("UserEvent: " + evt)

        if (evt.isInstanceOf[IdleStateEvent]) {        

            val e:IdleStateEvent = evt.asInstanceOf[IdleStateEvent];

            logger.debug("UserEvent: " + e)

            val msg = Unpooled.copiedBuffer("Raptor Ping".getBytes())
            val pingFrame = new PingWebSocketFrame(msg)
            ctx.channel().writeAndFlush(pingFrame);

            if (e.state() == IdleState.READER_IDLE) {
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                // ...   
            }
        }
    }
}

class WebSocketServerInitializer(actorSystem: ActorSystem, pushActor: ActorRef)(implicit ec: ExecutionContext) extends ChannelInitializer[SocketChannel] {

    val WEBSOCKET_PATH = "/websocket"
    val DEFAULT_CONNECT_TIMEOUT = 30

    val logger = LoggerFactory.getLogger(getClass)    

    @throws(classOf[Exception])
    override def initChannel(ch: SocketChannel) {

        logger.debug("InitChannel: " + ch + ", localAddress: " + ch.localAddress())

        ch.pipeline().addLast(
            new HttpRequestDecoder(),
            new HttpObjectAggregator(65536),
            new HttpResponseEncoder(),
            new WebSocketServerProtocolHandler("/websocket"),
            new WebSocketFrameHandler(pushActor));

        ch.pipeline.addFirst("idleStateHandler", new IdleStateHandler(0, 0, DEFAULT_CONNECT_TIMEOUT));
        ch.pipeline().addLast("myHandler", new MyHandler(pushActor));
    }
}