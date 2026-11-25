package com.elenai.elenaidodge.network.message;

import com.elenai.elenaidodge.ElenaiDodge;
import com.elenai.elenaidodge.capability.weight.IWeight;
import com.elenai.elenaidodge.capability.weight.WeightProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SWeightMessage implements IMessage {
   private boolean messageValid;
   private double weight;

   public SWeightMessage() {
      this.messageValid = false;
   }

   public SWeightMessage(double weight) {
      this.weight = weight;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      try {
         this.weight = buf.readDouble();
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

   public static class Handler implements IMessageHandler<SWeightMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(SWeightMessage message, MessageContext ctx) {
         if (!message.messageValid && ctx.side != Side.SERVER) {
            return null;
         } else {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
               this.processMessage(message, ctx);
            });
            return null;
         }
      }

      void processMessage(SWeightMessage message, MessageContext ctx) {
         EntityPlayerMP player = ctx.getServerHandler().player;
         IWeight w = (IWeight)player.getCapability(WeightProvider.WEIGHT_CAP, (EnumFacing)null);
         w.set(message.weight);
      }
   }
}
