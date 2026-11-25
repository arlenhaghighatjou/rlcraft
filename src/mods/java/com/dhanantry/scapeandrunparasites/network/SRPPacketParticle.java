package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import io.netty.buffer.ByteBuf;
import java.util.Random;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SRPPacketParticle implements IMessage {
   private double x;
   private double y;
   private double z;
   private float width;
   private float height;
   private byte type;

   public SRPPacketParticle() {
   }

   public SRPPacketParticle(double xx, double yy, double zz, float width, float height, byte particleType) {
      this.x = xx;
      this.y = yy;
      this.z = zz;
      this.width = width;
      this.height = height;
      this.type = particleType;
   }

   public void fromBytes(ByteBuf ByteBuf) {
      this.x = ByteBuf.readDouble();
      this.y = ByteBuf.readDouble();
      this.z = ByteBuf.readDouble();
      this.width = ByteBuf.readFloat();
      this.height = ByteBuf.readFloat();
      this.type = ByteBuf.readByte();
   }

   public void toBytes(ByteBuf ByteBuf) {
      ByteBuf.writeDouble(this.x);
      ByteBuf.writeDouble(this.y);
      ByteBuf.writeDouble(this.z);
      ByteBuf.writeFloat(this.width);
      ByteBuf.writeFloat(this.height);
      ByteBuf.writeByte(this.type);
   }

   public static class Handler implements IMessageHandler<SRPPacketParticle, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(SRPPacketParticle message, MessageContext ctx) {
         FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            this.handle(message, ctx);
         });
         return null;
      }

      private void handle(SRPPacketParticle message, MessageContext ctx) {
         switch(message.type) {
         case 1:
            this.spawnParticles(SRPEnumParticle.GSPLASH, new Random(), message, 0, 0, 0);
            break;
         case 2:
            for(int i = 0; i <= 3; ++i) {
               this.spawnParticles(SRPEnumParticle.GCLOUD, new Random(), message, 0, 0, 0);
            }

            return;
         case 3:
            for(int i = 0; i <= 5; ++i) {
               this.spawnParticles(SRPEnumParticle.RHAPPY, new Random(), message, 0, 0, 0);
            }

            return;
         case 4:
            for(int i = 0; i <= 23; ++i) {
               this.spawnParticles(SRPEnumParticle.GCLOUD, new Random(), message, 125, 227, 118);
            }
         case 5:
         }

      }

      private void spawnParticles(SRPEnumParticle particleType, Random rand, SRPPacketParticle message, int r, int g, int b) {
         double d0 = rand.nextGaussian() * 0.02D;
         double d1 = rand.nextGaussian() * 0.02D;
         double d2 = rand.nextGaussian() * 0.02D;
         ParticleSpawner.spawnParticle(particleType, message.x + (double)(rand.nextFloat() * message.width * 2.0F) - (double)message.width, message.y + 0.5D + (double)(rand.nextFloat() * message.height), message.z + (double)(rand.nextFloat() * message.width * 2.0F) - (double)message.width, d0, d1, d2, r, g, b);
      }
   }
}
