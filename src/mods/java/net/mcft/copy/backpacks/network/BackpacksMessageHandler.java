package net.mcft.copy.backpacks.network;

import net.mcft.copy.backpacks.misc.util.ClientUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class BackpacksMessageHandler<T extends IMessage> implements IMessageHandler<T, IMessage> {
   public BackpacksMessageHandler() {
   }

   public boolean isScheduled() {
      return true;
   }

   public abstract void handle(T var1, MessageContext var2);

   public final IMessage onMessage(T message, MessageContext ctx) {
      if (!this.isScheduled()) {
         this.handle(message, ctx);
      } else {
         getScheduler(ctx).addScheduledTask(() -> {
            this.handle(message, ctx);
         });
      }

      return null;
   }

   public static EntityPlayer getPlayer(MessageContext ctx) {
      return (EntityPlayer)(ctx.side.isServer() ? ctx.getServerHandler().player : ClientUtils.getPlayer());
   }

   public static World getWorld(MessageContext ctx) {
      return getPlayer(ctx).world;
   }

   public static IThreadListener getScheduler(MessageContext ctx) {
      return (IThreadListener)(ctx.side.isServer() ? (WorldServer)getWorld(ctx) : ClientUtils.getScheduler());
   }
}
