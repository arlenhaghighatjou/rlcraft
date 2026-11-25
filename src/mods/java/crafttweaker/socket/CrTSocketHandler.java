package crafttweaker.socket;

import crafttweaker.CraftTweakerAPI;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class CrTSocketHandler {
   public static final int PORT = 24532;
   public static CrTSocketHandler INSTANCE = null;
   private NioEventLoopGroup bossGroup;
   private NioEventLoopGroup workerGroup;

   public CrTSocketHandler() {
      (new Thread(this::handleServerSocket)).start();
   }

   public static void enableSocket() {
      if (INSTANCE == null) {
         INSTANCE = new CrTSocketHandler();
         CraftTweakerAPI.logInfo("Started ZsLint Socket lint on Port 24532");
      }

   }

   private void handleServerSocket() {
      try {
         this.bossGroup = new NioEventLoopGroup(1);
         this.workerGroup = new NioEventLoopGroup();
         ServerBootstrap b = new ServerBootstrap();
         ((ServerBootstrap)((ServerBootstrap)((ServerBootstrap)b.group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class)).childHandler(new CrTSocketInitializer()).option(ChannelOption.SO_BACKLOG, Integer.valueOf(512))).handler(new LoggingHandler(LogLevel.INFO))).childOption(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));
         Channel ch = b.bind(24532).sync().channel();
         ch.closeFuture().sync();
      } catch (InterruptedException var6) {
         CraftTweakerAPI.logError("Error while in Socket Thread", var6);
      } finally {
         this.bossGroup.shutdownGracefully();
         this.workerGroup.shutdownGracefully();
      }

   }
}
