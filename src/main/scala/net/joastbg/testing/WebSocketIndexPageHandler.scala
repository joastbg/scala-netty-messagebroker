package net.joastbg.testing

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http2.HttpUtil;

import io.netty.util.CharsetUtil;
  
import io.netty.handler.codec.http.HttpMethod.GET;
import io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import io.netty.handler.codec.http.HttpResponseStatus.OK;
import io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

class WebSocketIndexPageHandler(wsPath: String) extends SimpleChannelInboundHandler[FullHttpRequest] {
    val websocketPath: String = wsPath
    
    @throws(classOf[Exception])
    def channelRead0(ctx: ChannelHandlerContext, req: FullHttpRequest) {
        
          // Handle a bad request.
          if (!req.decoderResult().isSuccess()) {
              sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST))
              return
          }
  
          // Allow only GET methods.
          if (req.method() != GET) {
              sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN))
              return
          }
  
          // Send the index page
          if ("/".equals(req.uri()) || "/index.html".equals(req.uri())) {
              val webSocketLocation: String = getWebSocketLocation(ctx.pipeline(), req, websocketPath)
              val content: ByteBuf = WebSocketServerIndexPage.getContent(webSocketLocation)
              val res: FullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, content)
  
              res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8")
              //HttpUtil.setContentLength(res, content.readableBytes())
  
              sendHttpResponse(ctx, req, res)
          } else {
              sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND))
          }
    }

    override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        ctx.close()
    }

    def sendHttpResponse(ctx: ChannelHandlerContext, req: FullHttpRequest, res: FullHttpResponse) {

        // Generate an error page if response getStatus code is not OK (200).
        if (res.status().code() != 200) {
            val buf: ByteBuf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8)
            res.content().writeBytes(buf)
            buf.release()
            //HttpUtil.setContentLength(res, res.content().readableBytes())
        }

        // Send the response and close the connection if necessary.
        val f: ChannelFuture = ctx.channel().writeAndFlush(res)
        if (/*!HttpUtil.isKeepAlive(req) || */res.status().code() != 200) {
            //f.addListener(ChannelFutureListener.CLOSE)
        }
    }   

    def getWebSocketLocation(cp: ChannelPipeline, req: HttpRequest, path: String): String = {

        ("ws" + "://" + req.headers().get(HttpHeaderNames.HOST) + path)
    }

    @throws(classOf[Exception])
    override def messageReceived(ctx: ChannelHandlerContext, message: io.netty.handler.codec.http.FullHttpRequest) {
        println(">>>> " + message);
    }
}
