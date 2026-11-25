package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.item.tool.IHaveReach;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SRPPacketMeleeRange implements IMessage {
   private int entityId;

   public SRPPacketMeleeRange() {
   }

   public SRPPacketMeleeRange(int parEntityId) {
      this.entityId = parEntityId;
   }

   public void fromBytes(ByteBuf buf) {
      this.entityId = ByteBufUtils.readVarInt(buf, 4);
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeVarInt(buf, this.entityId, 4);
   }

   public static class Handler implements IMessageHandler<SRPPacketMeleeRange, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(final SRPPacketMeleeRange message, MessageContext ctx) {
         final EntityPlayerMP thePlayer = SRPMain.proxy.getPlayerEntityFromContext(ctx);
         thePlayer.getServer().addScheduledTask(new Runnable() {
            public void run() {
               Entity theEntity = thePlayer.world.getEntityByID(message.entityId);
               if (thePlayer.getActiveItemStack() != null && theEntity != null) {
                  if (thePlayer.getHeldItemMainhand().getItem() instanceof IHaveReach) {
                     IHaveReach theExtendedReachWeapon = (IHaveReach)thePlayer.getHeldItemMainhand().getItem();
                     double distanceSq = thePlayer.getDistanceSq(theEntity);
                     double reachSq = (double)(theExtendedReachWeapon.getReach() * theExtendedReachWeapon.getReach());
                     if (reachSq >= distanceSq) {
                        thePlayer.attackTargetEntityWithCurrentItem(theEntity);
                        thePlayer.world.playSound((EntityPlayer)null, thePlayer.posX, thePlayer.posY, thePlayer.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, thePlayer.getSoundCategory(), 1.0F, 1.0F);
                        thePlayer.spawnSweepParticles();
                     }
                  }

               }
            }
         });
         return null;
      }
   }
}
