package collision.packets;

import collision.handlers.ModConfig;
import collisiondamage.core.CollisionDamage;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketCollisionS implements IMessage {
   private double accel;

   public double getAccel() {
      return this.accel;
   }

   public PacketCollisionS() {
      this.accel = 0.0D;
   }

   public PacketCollisionS(double accel) {
      this.accel = accel;
   }

   public void fromBytes(ByteBuf buf) {
      this.accel = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeDouble(this.accel);
   }

   public static class CollisionMessageHandler implements IMessageHandler<PacketCollisionS, IMessage> {
      public CollisionMessageHandler() {
      }

      public IMessage onMessage(PacketCollisionS message, MessageContext ctx) {
         if (ctx.side == Side.SERVER) {
            EntityPlayer player = CollisionDamage.proxy.getPlayer(ctx);
            if (player == null) {
               return null;
            }

            double accel = message.getAccel();
            IThreadListener thread = CollisionDamage.proxy.getListener(ctx);
            thread.addScheduledTask(() -> {
               if (accel > ModConfig.server.accelerationThreshold) {
                  float damageValue = (float)Math.round((accel - ModConfig.server.accelerationThreshold) * 4.0D * ModConfig.server.damageMultiplier) / 4.0F;
                  player.playSound(damageValue > 4.0F ? SoundEvents.ENTITY_GENERIC_BIG_FALL : SoundEvents.ENTITY_GENERIC_SMALL_FALL, 1.0F, 1.0F);
                  player.attackEntityFrom(ModConfig.server.damageTypeWall ? DamageSource.FLY_INTO_WALL : DamageSource.FALL, damageValue);
               }

            });
         }

         return null;
      }
   }
}
