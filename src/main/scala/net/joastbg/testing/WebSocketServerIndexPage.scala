
package net.joastbg.testing

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

object WebSocketServerIndexPage {
    val NEWLINE = "\r\n";

    def getContent(webSocketLocation: String): ByteBuf = {
        Unpooled.copiedBuffer("</html>" + NEWLINE, CharsetUtil.US_ASCII)
    }
}