package com.elenai.elenaidodge.network.message;

import com.elenai.elenaidodge.ElenaiDodge;
import com.elenai.elenaidodge.util.PatronRewardHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CPatronMessage implements IMessage {
   private int level;
   private boolean messageValid;

   public CPatronMessage() {
      this.messageValid = false;
   }

   public CPatronMessage(int level) {
      this.level = level;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      try {
         this.level = buf.readInt();
      } catch (IndexOutOfBoundsException var3) {
         ElenaiDodge.LOG.error("Error occured whilst networking!", var3);
         return;
      }

      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      if (this.messageValid) {
         buf.writeInt(this.level);
      }
   }

   public static class Handler implements IMessageHandler<CPatronMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(CPatronMessage message, MessageContext ctx) {
         if (!message.messageValid && ctx.side != Side.CLIENT) {
            return null;
         } else {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
               this.processMessage(message, ctx);
            });
            return null;
         }
      }

      void processMessage(CPatronMessage message, MessageContext ctx) {
         if (message.level == 99) {
            PatronRewardHandler.localPatronTier = 4;
         } else {
            PatronRewardHandler.localPatronTier = message.level;
         }

      }
   }
}
