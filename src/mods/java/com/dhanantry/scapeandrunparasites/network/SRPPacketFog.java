package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.util.handlers.SRPEventHandlerBus;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SRPPacketFog implements IMessage {
   private float targetId;

   public SRPPacketFog() {
   }

   public SRPPacketFog(float target) {
      this.targetId = target;
   }

   public void fromBytes(ByteBuf buf) {
      this.targetId = buf.readFloat();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeFloat(this.targetId);
   }

   public static class Handler implements IMessageHandler<SRPPacketFog, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(SRPPacketFog message, MessageContext ctx) {
         FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            this.handle(message, ctx);
         });
         return null;
      }

      private void handle(SRPPacketFog message, MessageContext ctx) {
         SRPEventHandlerBus.fog = message.targetId;
      }
   }
}
