package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.grapplemod;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GrappleDetachMessage implements IMessage {
   public int id;

   public GrappleDetachMessage() {
   }

   public GrappleDetachMessage(int id) {
      this.id = id;
   }

   public void fromBytes(ByteBuf buf) {
      this.id = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.id);
   }

   public static class Handler implements IMessageHandler<GrappleDetachMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(GrappleDetachMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new GrappleDetachMessage.Handler.runner(message, ctx));
         return null;
      }

      public class runner implements Runnable {
         GrappleDetachMessage message;
         MessageContext ctx;

         public runner(GrappleDetachMessage message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
         }

         public void run() {
            grapplemod.receiveGrappleDetach(this.message.id);
         }
      }
   }
}
