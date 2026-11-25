package artifacts.common.network;

import artifacts.common.init.ModSoundEvents;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBottledCloudJump implements IMessage {
   boolean isFart;

   public PacketBottledCloudJump() {
   }

   public PacketBottledCloudJump(boolean isFart) {
      this.isFart = isFart;
   }

   public void fromBytes(ByteBuf buf) {
      this.isFart = buf.readBoolean();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.isFart);
   }

   public static class PacketHandler implements IMessageHandler<PacketBottledCloudJump, IMessage> {
      public PacketHandler() {
      }

      public IMessage onMessage(PacketBottledCloudJump message, MessageContext ctx) {
         EntityPlayerMP player = ctx.getServerHandler().player;
         player.getServerWorld().addScheduledTask(() -> {
            player.fallDistance = 0.0F;
            player.jump();

            for(int x = -1; x <= 1; ++x) {
               for(int z = -1; z <= 1; ++z) {
                  if (x != 0 && z != 0) {
                     player.world.spawnParticle(EnumParticleTypes.CLOUD, player.posX + (double)x / 1.5D, player.posY, player.posZ + (double)z / 1.5D, 0.0D, 0.0D, 0.0D, new int[0]);
                  } else {
                     player.world.spawnParticle(EnumParticleTypes.CLOUD, player.posX + (double)x, player.posY, player.posZ + (double)z, 0.0D, 0.0D, 0.0D, new int[0]);
                  }
               }
            }

            if (message.isFart) {
               player.playSound(ModSoundEvents.FART, 1.3F, 0.8F + player.getRNG().nextFloat() * 0.4F);
            } else {
               player.playSound(SoundEvents.BLOCK_CLOTH_FALL, 1.3F, 0.8F + player.getRNG().nextFloat() * 0.4F);
            }

         });
         return null;
      }
   }
}
