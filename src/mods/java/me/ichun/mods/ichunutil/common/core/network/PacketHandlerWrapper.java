package me.ichun.mods.ichunutil.common.core.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class PacketHandlerWrapper<REQ extends AbstractPacket> extends SimpleChannelInboundHandler<REQ> {
   private final Side side;

   public PacketHandlerWrapper(Side side, Class<REQ> requestType) {
      super(requestType);
      this.side = side;
   }

   protected void channelRead0(ChannelHandlerContext ctx, REQ msg) throws Exception {
      EntityPlayer player;
      if (this.side.isServer()) {
         INetHandler netHandler = (INetHandler)ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
         player = ((NetHandlerPlayServer)netHandler).player;
      } else {
         player = this.getClientPlayer();
      }

      if (!msg.requiresMainThread()) {
         this.executeMessage(msg, player, this.side, ctx);
      } else {
         IThreadListener thread = FMLCommonHandler.instance().getWorldThread((INetHandler)ctx.channel().attr(NetworkRegistry.NET_HANDLER).get());
         if (thread.isCallingFromMinecraftThread()) {
            this.executeMessage(msg, player, this.side, ctx);
         } else {
            thread.addScheduledTask(() -> {
               this.executeMessage(msg, player, this.side, ctx);
            });
         }
      }

   }

   public void executeMessage(AbstractPacket msg, EntityPlayer player, Side side, ChannelHandlerContext ctx) {
      msg.execute(side, player);
   }

   @SideOnly(Side.CLIENT)
   public EntityPlayer getClientPlayer() {
      return Minecraft.getMinecraft().player;
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      FMLLog.log(Level.ERROR, cause, "iChunUtil Packet exception", new Object[0]);
      super.exceptionCaught(ctx, cause);
   }
}
