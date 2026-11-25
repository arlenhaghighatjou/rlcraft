package com.elenai.elenaidodge.network.message;

import com.elenai.elenaidodge.ElenaiDodge;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CVelocityMessage implements IMessage {
   private double motionX;
   private double motionY;
   private double motionZ;
   private boolean messageValid;
   private boolean addVelocity;

   public CVelocityMessage() {
      this.messageValid = false;
   }

   public CVelocityMessage(double motionX, double motionY, double motionZ, boolean addVelocity) {
      this.motionX = motionX;
      this.motionY = motionY;
      this.motionZ = motionZ;
      this.addVelocity = addVelocity;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      try {
         this.motionX = buf.readDouble();
         this.motionY = buf.readDouble();
         this.motionZ = buf.readDouble();
         this.addVelocity = buf.readBoolean();
      } catch (IndexOutOfBoundsException var3) {
         ElenaiDodge.LOG.error("Error occured whilst networking!", var3);
         return;
      }

      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      if (this.messageValid) {
         buf.writeDouble(this.motionX);
         buf.writeDouble(this.motionY);
         buf.writeDouble(this.motionZ);
         buf.writeBoolean(this.addVelocity);
      }
   }

   public static class Handler implements IMessageHandler<CVelocityMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(CVelocityMessage message, MessageContext ctx) {
         if (!message.messageValid && ctx.side != Side.CLIENT) {
            return null;
         } else {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
               this.processMessage(message, ctx);
            });
            return null;
         }
      }

      void processMessage(CVelocityMessage message, MessageContext ctx) {
         if (message.addVelocity) {
            Minecraft.getMinecraft().player.addVelocity(message.motionX, message.motionY, message.motionZ);
         } else {
            Minecraft.getMinecraft().player.setVelocity(message.motionX, message.motionY, message.motionZ);
         }

      }
   }
}
