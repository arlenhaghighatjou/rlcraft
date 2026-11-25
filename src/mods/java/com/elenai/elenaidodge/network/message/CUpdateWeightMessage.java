package com.elenai.elenaidodge.network.message;

import com.elenai.elenaidodge.ElenaiDodge;
import com.elenai.elenaidodge.event.ArmorTickEventListener;
import com.elenai.elenaidodge.util.ClientStorage;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CUpdateWeightMessage implements IMessage {
   private double weight;
   private boolean messageValid;

   public CUpdateWeightMessage() {
      this.messageValid = false;
   }

   public CUpdateWeightMessage(double weight) {
      this.weight = weight;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      try {
         this.weight = (double)buf.readInt();
      } catch (IndexOutOfBoundsException var3) {
         ElenaiDodge.LOG.error("Error occured whilst networking!", var3);
         return;
      }

      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      if (this.messageValid) {
         buf.writeDouble(this.weight);
      }
   }

   public static class Handler implements IMessageHandler<CUpdateWeightMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(CUpdateWeightMessage message, MessageContext ctx) {
         if (!message.messageValid && ctx.side != Side.CLIENT) {
            return null;
         } else {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
               this.processMessage(message, ctx);
            });
            return null;
         }
      }

      void processMessage(CUpdateWeightMessage message, MessageContext ctx) {
         if (message.weight != 0.0D) {
            ClientStorage.weight = message.weight;
         } else {
            ArmorTickEventListener.previousArmor.clear();
         }

      }
   }
}
