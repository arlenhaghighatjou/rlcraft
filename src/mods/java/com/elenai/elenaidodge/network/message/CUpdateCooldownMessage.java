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

public class CUpdateCooldownMessage implements IMessage {
   private int regenRate;
   private boolean messageValid;

   public CUpdateCooldownMessage() {
      this.messageValid = false;
   }

   public CUpdateCooldownMessage(int regenRate) {
      this.regenRate = regenRate;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      try {
         this.regenRate = buf.readInt();
      } catch (IndexOutOfBoundsException var3) {
         ElenaiDodge.LOG.error("Error occured whilst networking!", var3);
         return;
      }

      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      if (this.messageValid) {
         buf.writeInt(this.regenRate);
      }
   }

   public static class Handler implements IMessageHandler<CUpdateCooldownMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(CUpdateCooldownMessage message, MessageContext ctx) {
         if (!message.messageValid && ctx.side != Side.CLIENT) {
            return null;
         } else {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
               this.processMessage(message, ctx);
            });
            return null;
         }
      }

      void processMessage(CUpdateCooldownMessage message, MessageContext ctx) {
         ClientStorage.regenSpeed = message.regenRate;
         ArmorTickEventListener.previousArmor.clear();
      }
   }
}
