package com.elenai.elenaidodge.network.message;

import com.elenai.elenaidodge.ElenaiDodge;
import com.elenai.elenaidodge.util.ClientStorage;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CUpdateRegenMessage implements IMessage {
   private int regen;
   private boolean messageValid;

   public CUpdateRegenMessage() {
      this.messageValid = false;
   }

   public CUpdateRegenMessage(int regen) {
      this.regen = regen;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      try {
         this.regen = buf.readInt();
      } catch (IndexOutOfBoundsException var3) {
         ElenaiDodge.LOG.error("Error occured whilst networking!", var3);
         return;
      }

      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      if (this.messageValid) {
         buf.writeInt(this.regen);
      }
   }

   public static class Handler implements IMessageHandler<CUpdateRegenMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(CUpdateRegenMessage message, MessageContext ctx) {
         if (!message.messageValid && ctx.side != Side.CLIENT) {
            return null;
         } else {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
               this.processMessage(message, ctx);
            });
            return null;
         }
      }

      void processMessage(CUpdateRegenMessage message, MessageContext ctx) {
         ClientStorage.regenModifier = message.regen;
      }
   }
}
