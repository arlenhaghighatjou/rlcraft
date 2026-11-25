package c4.corpsecomplex.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TeleportEffectMessage implements IMessage {
   private static EaglercraftRandom rand = new EaglercraftRandom();
   private BlockPos pos;

   public TeleportEffectMessage() {
   }

   public TeleportEffectMessage(BlockPos pos) {
      this.pos = pos;
   }

   public void toBytes(ByteBuf buf) {
      buf.writeLong(this.pos.toLong());
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = BlockPos.fromLong(buf.readLong());
   }

   public static class TeleportEffectMessageHandler implements IMessageHandler<TeleportEffectMessage, IMessage> {
      public TeleportEffectMessageHandler() {
      }

      @SideOnly(Side.CLIENT)
      public IMessage onMessage(TeleportEffectMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(() -> {
            EntityPlayerSP playerSP = Minecraft.getMinecraft().player;

            for(int x = 0; x < 128; ++x) {
               float f = (TeleportEffectMessage.rand.nextFloat() - 0.5F) * 0.2F;
               float f1 = (TeleportEffectMessage.rand.nextFloat() - 0.5F) * 0.2F;
               float f2 = (TeleportEffectMessage.rand.nextFloat() - 0.5F) * 0.2F;
               double d3 = (double)message.pos.getX() + (TeleportEffectMessage.rand.nextDouble() - 0.5D) * (double)playerSP.width * 2.0D;
               double d4 = (double)message.pos.getY() + TeleportEffectMessage.rand.nextDouble() * (double)playerSP.height;
               double d5 = (double)message.pos.getZ() + (TeleportEffectMessage.rand.nextDouble() - 0.5D) * (double)playerSP.width * 2.0D;
               playerSP.world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (double)f, (double)f1, (double)f2, new int[0]);
            }

         });
         return null;
      }
   }
}
