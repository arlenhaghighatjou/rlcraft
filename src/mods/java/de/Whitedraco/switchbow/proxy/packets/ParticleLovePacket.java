package de.Whitedraco.switchbow.proxy.packets;

import io.netty.buffer.ByteBuf;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ParticleLovePacket implements IMessage {
   protected Random rand = new Random();
   double posX;
   double posY;
   double posZ;
   double width;
   double height;

   public ParticleLovePacket() {
   }

   public ParticleLovePacket(double posX, double posY, double posZ, double width, double height) {
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.width = width;
      this.height = height;
   }

   public void fromBytes(ByteBuf buf) {
      this.posX = buf.readDouble();
      this.posY = buf.readDouble();
      this.posZ = buf.readDouble();
      this.width = buf.readDouble();
      this.height = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeDouble(this.posX);
      buf.writeDouble(this.posY);
      buf.writeDouble(this.posZ);
      buf.writeDouble(this.width);
      buf.writeDouble(this.height);
   }

   public void execute(MessageContext ctx) {
      double d0 = this.rand.nextGaussian() * 0.02D;
      double d1 = this.rand.nextGaussian() * 0.02D;
      double d2 = this.rand.nextGaussian() * 0.02D;
      Minecraft.getMinecraft().world.spawnParticle(EnumParticleTypes.HEART, this.posX + (double)this.rand.nextFloat() * this.width * 2.0D - this.width, this.posY + 0.5D + (double)this.rand.nextFloat() * this.height, this.posZ + (double)this.rand.nextFloat() * this.width * 2.0D - this.width, d0, d1, d2, new int[0]);
   }

   public static class Handler implements IMessageHandler<ParticleLovePacket, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(ParticleLovePacket message, MessageContext ctx) {
         message.execute(ctx);
         return null;
      }
   }
}
