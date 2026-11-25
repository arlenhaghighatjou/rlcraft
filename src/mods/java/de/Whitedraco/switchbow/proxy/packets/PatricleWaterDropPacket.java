package de.Whitedraco.switchbow.proxy.packets;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PatricleWaterDropPacket implements IMessage {
   double posX;
   double posY;
   double posZ;
   double d0;
   double d1;
   double d2;

   public PatricleWaterDropPacket() {
   }

   public PatricleWaterDropPacket(double posX, double posY, double posZ, double d0, double d1, double d2) {
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.d0 = d0;
      this.d1 = d1;
      this.d2 = d2;
   }

   public void fromBytes(ByteBuf buf) {
      this.posX = buf.readDouble();
      this.posY = buf.readDouble();
      this.posZ = buf.readDouble();
      this.d0 = buf.readDouble();
      this.d1 = buf.readDouble();
      this.d2 = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeDouble(this.posX);
      buf.writeDouble(this.posY);
      buf.writeDouble(this.posZ);
      buf.writeDouble(this.d0);
      buf.writeDouble(this.d1);
      buf.writeDouble(this.d2);
   }

   public void execute(MessageContext ctx) {
      int[] a = new int[0];
      Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX, this.posY, this.posZ, this.d0, this.d1, this.d2, a);
   }

   public static class Handler implements IMessageHandler<PatricleWaterDropPacket, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(PatricleWaterDropPacket message, MessageContext ctx) {
         message.execute(ctx);
         return null;
      }
   }
}
