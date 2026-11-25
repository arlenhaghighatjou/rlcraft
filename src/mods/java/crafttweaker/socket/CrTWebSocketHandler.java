package crafttweaker.socket;

import crafttweaker.CraftTweakerAPI;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class CrTWebSocketHandler extends ChannelInboundHandlerAdapter {
   public CrTWebSocketHandler() {
   }

   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if (msg instanceof WebSocketFrame) {
         CraftTweakerAPI.logInfo("This is a WebSocket frame");
         CraftTweakerAPI.logInfo("Client Channel : " + ctx.channel());
         if (msg instanceof BinaryWebSocketFrame) {
            CraftTweakerAPI.logInfo("BinaryWebSocketFrame Received : ");
            CraftTweakerAPI.logInfo(String.valueOf(((BinaryWebSocketFrame)msg).content()));
         } else if (msg instanceof TextWebSocketFrame) {
            CraftTweakerAPI.logInfo("TextWebSocketFrame Received : ");
            String text = ((TextWebSocketFrame)msg).text();
            if (!text.startsWith("{")) {
               CraftTweakerAPI.logInfo("No valid json request: " + text);
               return;
            }

            TextWebSocketFrame res = new TextWebSocketFrame(JsonMessageHandler.handleJson(text, ctx));
            ctx.channel().writeAndFlush(res);
            CraftTweakerAPI.logInfo(((TextWebSocketFrame)msg).text());
         } else if (msg instanceof PingWebSocketFrame) {
            CraftTweakerAPI.logInfo("PingWebSocketFrame Received : ");
            CraftTweakerAPI.logInfo(String.valueOf(((PingWebSocketFrame)msg).content()));
         } else if (msg instanceof PongWebSocketFrame) {
            CraftTweakerAPI.logInfo("PongWebSocketFrame Received : ");
            CraftTweakerAPI.logInfo(String.valueOf(((PongWebSocketFrame)msg).content()));
         } else if (msg instanceof CloseWebSocketFrame) {
            CraftTweakerAPI.logInfo("CloseWebSocketFrame Received : ");
            CraftTweakerAPI.logInfo("ReasonText :" + ((CloseWebSocketFrame)msg).reasonText());
            CraftTweakerAPI.logInfo("StatusCode : " + ((CloseWebSocketFrame)msg).statusCode());
         } else {
            CraftTweakerAPI.logWarning("Unsupported WebSocketFrame");
         }
      }

   }
}
