package com.elenai.elenaidodge.network.message;

import com.elenai.elenaidodge.effects.ClientDodgeEffects;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CDodgeEffectsMessage implements IMessage {
   private boolean messageValid = true;

   public CDodgeEffectsMessage() {
   }

   public void fromBytes(ByteBuf buf) {
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      if (this.messageValid) {
         ;
      }
   }

   public static class Handler implements IMessageHandler<CDodgeEffectsMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(CDodgeEffectsMessage message, MessageContext ctx) {
         if (!message.messageValid && ctx.side != Side.CLIENT) {
            return null;
         } else {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
               this.processMessage(message, ctx);
            });
            return null;
         }
      }

      void processMessage(CDodgeEffectsMessage message, MessageContext ctx) {
         ClientDodgeEffects.run();
      }
   }
}
