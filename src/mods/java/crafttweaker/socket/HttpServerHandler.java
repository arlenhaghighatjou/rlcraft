package crafttweaker.socket;

import crafttweaker.CraftTweakerAPI;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import java.net.URISyntaxException;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {
   public HttpServerHandler() {
   }

   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if (msg instanceof FullHttpMessage) {
         CraftTweakerAPI.logInfo("Full HTTP Message Received");
      } else if (msg instanceof HttpRequest) {
         HttpRequest httpRequest = (HttpRequest)msg;
         CraftTweakerAPI.logInfo("Http Request Received");
         HttpHeaders headers = httpRequest.headers();
         CraftTweakerAPI.logInfo("Connection : " + headers.get("Connection"));
         CraftTweakerAPI.logInfo("Upgrade : " + headers.get("Upgrade"));
         if (headers.get("Connection").equalsIgnoreCase("Upgrade") || headers.get("Upgrade").equalsIgnoreCase("WebSocket")) {
            ctx.pipeline().replace(this, "websocketHandler", new CrTWebSocketHandler());
            CraftTweakerAPI.logInfo("CrTWebSocketHandler added to the pipeline");
            CraftTweakerAPI.logInfo("Opened Channel : " + ctx.channel());
            CraftTweakerAPI.logInfo("Handshaking....");
            this.handleHandshake(ctx, httpRequest);
            CraftTweakerAPI.logInfo("Handshake is done");
         }
      } else {
         CraftTweakerAPI.logWarning("Incoming request is unknown");
      }

   }

   protected void handleHandshake(ChannelHandlerContext ctx, HttpRequest req) throws URISyntaxException {
      WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(this.getWebSocketURL(req), "zslint", true);
      WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
      if (handshaker == null) {
         WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
      } else {
         handshaker.handshake(ctx.channel(), req);
      }

   }

   protected String getWebSocketURL(HttpRequest req) {
      CraftTweakerAPI.logInfo("Req URI : " + req.uri());
      String url = "ws://" + req.headers().get("Host") + req.uri();
      CraftTweakerAPI.logInfo("Constructed URL : " + url);
      return url;
   }
}
