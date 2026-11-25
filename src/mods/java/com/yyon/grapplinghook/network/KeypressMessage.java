package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.items.KeypressItem;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class KeypressMessage implements IMessage {
   KeypressItem.Keys key;
   boolean isDown;

   public KeypressMessage() {
   }

   public KeypressMessage(KeypressItem.Keys thekey, boolean isDown) {
      this.key = thekey;
      this.isDown = isDown;
   }

   public void fromBytes(ByteBuf buf) {
      this.key = KeypressItem.Keys.values()[buf.readInt()];
      this.isDown = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.key.ordinal());
      buf.writeBoolean(this.isDown);
   }

   public static class Handler implements IMessageHandler<KeypressMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(KeypressMessage message, MessageContext ctx) {
         IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.world;
         mainThread.addScheduledTask(new KeypressMessage.Handler.runner(message, ctx));
         return null;
      }

      public class runner implements Runnable {
         KeypressMessage message;
         MessageContext ctx;

         public runner(KeypressMessage message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
         }

         public void run() {
            World w = this.ctx.getServerHandler().player.world;
            grapplemod.receiveKeypress(this.ctx.getServerHandler().player, this.message.key, this.message.isDown);
         }
      }
   }
}
