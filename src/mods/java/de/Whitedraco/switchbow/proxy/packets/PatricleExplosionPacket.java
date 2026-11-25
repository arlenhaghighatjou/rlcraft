package de.Whitedraco.switchbow.proxy.packets;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PatricleExplosionPacket implements IMessage {
   double posX;
   double posY;
   double posZ;
   double d0;
   double d1;
   double d2;
   double d3;
   double d4;
   double d5;

   public PatricleExplosionPacket() {
   }

   public PatricleExplosionPacket(double posX, double posY, double posZ, double d0, double d1, double d2, double d3, double d4, double d5) {
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.d0 = d0;
      this.d1 = d1;
      this.d2 = d2;
      this.d3 = d3;
      this.d4 = d4;
      this.d5 = d5;
   }

   public void fromBytes(ByteBuf buf) {
      this.posX = buf.readDouble();
      this.posY = buf.readDouble();
      this.posZ = buf.readDouble();
      this.d0 = buf.readDouble();
      this.d1 = buf.readDouble();
      this.d2 = buf.readDouble();
      this.d3 = buf.readDouble();
      this.d4 = buf.readDouble();
      this.d5 = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeDouble(this.posX);
      buf.writeDouble(this.posY);
      buf.writeDouble(this.posZ);
      buf.writeDouble(this.d0);
      buf.writeDouble(this.d1);
      buf.writeDouble(this.d2);
      buf.writeDouble(this.d3);
      buf.writeDouble(this.d4);
      buf.writeDouble(this.d5);
   }

   public void execute(MessageContext ctx) {
      int[] a = new int[0];
      double var10002 = (this.d0 + this.posX) / 2.0D;
      double var10003 = (this.d1 + this.posY) / 2.0D;
      Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, var10002, var10003, (this.d2 + this.posZ) / 2.0D, this.d3, this.d4, this.d5, new int[0]);
      Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.d0, this.d1, this.d2, this.d3, this.d4, this.d5, new int[0]);
   }

   public static class Handler implements IMessageHandler<PatricleExplosionPacket, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(PatricleExplosionPacket message, MessageContext ctx) {
         message.execute(ctx);
         return null;
      }
   }
}
