package ivorius.ivtoolkit.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SchedulingMessageHandler<REQ extends IMessage, REPLY extends IMessage> implements IMessageHandler<REQ, REPLY> {
   public SchedulingMessageHandler() {
   }

   public REPLY onMessage(REQ message, MessageContext ctx) {
      if (ctx.side.isClient()) {
         this.onMessageClient(message, ctx);
      } else if (ctx.side.isServer()) {
         this.onMessageServer(message, ctx);
      }

      return (REPLY)null;
   }

   private void onMessageServer(REQ message, MessageContext ctx) {
      WorldServer world = this.getServerWorld(message, ctx);
      world.addScheduledTask(() -> {
         this.processServer(message, ctx, world);
      });
   }

   @SideOnly(Side.CLIENT)
   private void onMessageClient(REQ message, MessageContext ctx) {
      Minecraft.getMinecraft().addScheduledTask(() -> {
         this.processClient(message, ctx);
      });
   }

   public WorldServer getServerWorld(REQ message, MessageContext ctx) {
      return (WorldServer)ctx.getServerHandler().player.world;
   }

   @SideOnly(Side.CLIENT)
   public void processClient(REQ message, MessageContext ctx) {
   }

   public void processServer(REQ message, MessageContext ctx, WorldServer world) {
   }
}
