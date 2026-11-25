package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SRPPacketEntityBodyHit implements IMessage {
   private int targetId;
   private int partId;

   public SRPPacketEntityBodyHit() {
   }

   public SRPPacketEntityBodyHit(int target, int part) {
      this.targetId = target;
      this.partId = part;
   }

   public void fromBytes(ByteBuf buf) {
      this.targetId = ByteBufUtils.readVarInt(buf, 4);
      this.partId = ByteBufUtils.readVarInt(buf, 4);
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeVarInt(buf, this.targetId, 4);
      ByteBufUtils.writeVarInt(buf, this.partId, 4);
   }

   public static class Handler implements IMessageHandler<SRPPacketEntityBodyHit, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(final SRPPacketEntityBodyHit message, MessageContext ctx) {
         final EntityPlayerMP thePlayer = SRPMain.proxy.getPlayerEntityFromContext(ctx);
         thePlayer.getServer().addScheduledTask(new Runnable() {
            public void run() {
               Entity target = thePlayer.world.getEntityByID(message.targetId);
               if (target != null) {
                  if (target instanceof EntityBodyParts) {
                     float dmg = (float)thePlayer.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                     float f3 = thePlayer.getCooledAttackStrength(0.0F) * dmg;
                     EntityBodyParts rTarget = (EntityBodyParts)target;
                     rTarget.attackEntityBodyFrom(DamageSource.causePlayerDamage(thePlayer), f3, message.partId, true);
                  }

               }
            }
         });
         return null;
      }
   }
}
