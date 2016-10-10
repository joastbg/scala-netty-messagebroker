package net.joastbg.testing

import java.util.Locale;
  
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WebSocketFrameHandler extends SimpleChannelInboundHandler[WebSocketFrame] {

    @throws(classOf[Exception])
    def channelRead0(ctx: ChannelHandlerContext, frame: WebSocketFrame) {

        println(" **** WebSocketFrameHandler::channelRead0")

        if (frame.isInstanceOf[TextWebSocketFrame]) {
              // Send the uppercase string back.
              val request: String = frame.asInstanceOf[TextWebSocketFrame].text();
              //logger.info("{} received {}", ctx.channel(), request);
              println(">>>> " + frame)    
              println(">>>> " + request)
    
              ctx.channel().writeAndFlush(new TextWebSocketFrame("hello world"));

          } else {
              val message: String = "unsupported frame type: " + frame.getClass().getName();
              throw new UnsupportedOperationException(message);
          }
    }

    @throws(classOf[Exception])
    def messageReceived(ctx: ChannelHandlerContext, message: io.netty.handler.codec.http.websocketx.WebSocketFrame) {
        println(">>>> " + message);

        if (message.isInstanceOf[TextWebSocketFrame]) {
            // Send the uppercase string back.
            val request: String = message.asInstanceOf[TextWebSocketFrame].text();
            //logger.info("{} received {}", ctx.channel(), request);
            println(">>>> " + message)    
            println(">>>> " + request)

            ctx.channel().writeAndFlush(new TextWebSocketFrame("hello world"));
        }
    }
}