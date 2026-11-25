package de.Whitedraco.switchbow.proxy.packets;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ParticleLightningPacket implements IMessage {
   double posX;
   double posY;
   double posZ;

   public ParticleLightningPacket() {
   }

   public ParticleLightningPacket(BlockPos pos) {
      this.posX = (double)pos.getX();
      this.posY = (double)pos.getY();
      this.posZ = (double)pos.getZ();
   }

   public void fromBytes(ByteBuf buf) {
      this.posX = buf.readDouble();
      this.posY = buf.readDouble();
      this.posZ = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeDouble(this.posX);
      buf.writeDouble(this.posY);
      buf.writeDouble(this.posZ);
   }

   @SideOnly(Side.CLIENT)
   public void execute(MessageContext ctx) {
      if (ctx.side.isClient()) {
         new EaglercraftRandom();
         int FPS = Minecraft.getDebugFPS();
         WorldClient world = Minecraft.getMinecraft().world;
         BlockPos posRender = new BlockPos(this.posX, this.posY, this.posZ);
         posRender = posRender.add(-2, 0, -2);
         if (FPS >= 25) {
            for(int i = 0; i < 5; ++i) {
               for(int p = 0; p < 5; ++p) {
                  BlockPos posRendering = posRender.add(i, 0, p);
                  world.addWeatherEffect(new EntityLightningBolt(world, (double)posRendering.getX(), (double)posRendering.getY(), (double)posRendering.getZ(), true));
               }
            }
         }
      }

   }

   public static class Handler implements IMessageHandler<ParticleLightningPacket, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(ParticleLightningPacket message, MessageContext ctx) {
         message.execute(ctx);
         return null;
      }
   }
}
