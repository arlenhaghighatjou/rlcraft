package crafttweaker.socket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class CrTSocketInitializer extends ChannelInitializer<SocketChannel> {
   public CrTSocketInitializer() {
   }

   protected void initChannel(SocketChannel ch) {
      ChannelPipeline pipeline = ch.pipeline();
      pipeline.addLast("httpServerCodec", new HttpServerCodec());
      pipeline.addLast("httpHandler", new HttpServerHandler());
   }
}
