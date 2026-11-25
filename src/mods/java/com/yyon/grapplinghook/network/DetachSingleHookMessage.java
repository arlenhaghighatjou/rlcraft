package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.grapplemod;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class DetachSingleHookMessage implements IMessage {
   public int id;
   public int hookid;

   public DetachSingleHookMessage() {
   }

   public DetachSingleHookMessage(int id, int hookid) {
      this.id = id;
      this.hookid = hookid;
   }

   public void fromBytes(ByteBuf buf) {
      this.id = buf.readInt();
      this.hookid = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.id);
      buf.writeInt(this.hookid);
   }

   public static class Handler implements IMessageHandler<DetachSingleHookMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(DetachSingleHookMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new DetachSingleHookMessage.Handler.runner(message, ctx));
         return null;
      }

      public class runner implements Runnable {
         DetachSingleHookMessage message;
         MessageContext ctx;

         public runner(DetachSingleHookMessage message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
         }

         public void run() {
            grapplemod.receiveGrappleDetachHook(this.message.id, this.message.hookid);
         }
      }
   }
}
