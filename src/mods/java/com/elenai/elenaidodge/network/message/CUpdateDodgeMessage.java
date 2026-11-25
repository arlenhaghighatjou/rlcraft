package com.elenai.elenaidodge.network.message;

import com.elenai.elenaidodge.ElenaiDodge;
import com.elenai.elenaidodge.util.ClientStorage;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CUpdateDodgeMessage implements IMessage {
   private int dodges;
   private boolean messageValid;

   public CUpdateDodgeMessage() {
      this.messageValid = false;
   }

   public CUpdateDodgeMessage(int dodges) {
      this.dodges = dodges;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      try {
         this.dodges = buf.readInt();
      } catch (IndexOutOfBoundsException var3) {
         ElenaiDodge.LOG.error("Error occured whilst networking!", var3);
         return;
      }

      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      if (this.messageValid) {
         buf.writeInt(this.dodges);
      }
   }

   public static class Handler implements IMessageHandler<CUpdateDodgeMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(CUpdateDodgeMessage message, MessageContext ctx) {
         if (!message.messageValid && ctx.side != Side.CLIENT) {
            return null;
         } else {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
               this.processMessage(message, ctx);
            });
            return null;
         }
      }

      void processMessage(CUpdateDodgeMessage message, MessageContext ctx) {
         ClientStorage.dodges = message.dodges;
      }
   }
}
