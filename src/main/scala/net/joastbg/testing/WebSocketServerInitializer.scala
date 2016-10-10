
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


class WebSocketServerInitializer extends ChannelInitializer[SocketChannel] {

    val WEBSOCKET_PATH = "/websocket";

    @throws(classOf[Exception])
    override def initChannel(ch: SocketChannel) {

        println(" **** WebSocketServerInitializer::initChannel")

        //var pipeline:ChannelPipeline = ch.pipeline();
        ch.pipeline().addLast(
                    new HttpRequestDecoder(),
                    new HttpObjectAggregator(65536),
                    new HttpResponseEncoder(),
                    new WebSocketServerProtocolHandler("/websocket"),
                    new WebSocketFrameHandler());
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