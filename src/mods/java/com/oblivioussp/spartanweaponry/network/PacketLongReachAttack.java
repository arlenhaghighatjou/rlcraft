package com.oblivioussp.spartanweaponry.network;

import com.oblivioussp.spartanweaponry.api.IWeaponPropertyContainer;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.util.Log;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketLongReachAttack extends PacketBase<PacketLongReachAttack> {
   private int entityId;
   private float velocity;

   public PacketLongReachAttack() {
   }

   public PacketLongReachAttack(int entId, float vel) {
      this.entityId = entId;
      this.velocity = vel;
      Log.trace("PacketLongReachAttack constructed");
   }

   public void fromBytes(ByteBuf buf) {
      this.entityId = ByteBufUtils.readVarInt(buf, 4);
      this.velocity = buf.readFloat();
      Log.trace("Read data from byte buffer to packet.");
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeVarInt(buf, this.entityId, 4);
      buf.writeFloat(this.velocity);
      Log.trace("Wrote packet to byte buffer.");
   }

   public void handleClientSide(PacketLongReachAttack message, EntityPlayer player) {
   }

   public void handleServerSide(PacketLongReachAttack message, EntityPlayerMP player) {
      if (message != null && player != null) {
         Entity victim = player.world.getEntityByID(message.entityId);
         if (victim != null) {
            Log.debug("Victim of attack: " + victim.toString());
            ItemStack weapon = player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
            if (!weapon.isEmpty()) {
               if (weapon.getItem() instanceof IWeaponPropertyContainer) {
                  WeaponProperty reachProp = ((IWeaponPropertyContainer)weapon.getItem()).getFirstWeaponPropertyWithType("reach");
                  if (reachProp != null) {
                     float reach = reachProp.getMagnitude();
                     double distanceSquared = player.getDistanceSq(victim);
                     double reachSquared = (double)(reach * reach);
                     player.getEntityData().setFloat("spartanweaponry_RidingVelocity", message.velocity);
                     if (reachSquared >= distanceSquared) {
                        player.attackTargetEntityWithCurrentItem(victim);
                        Log.debug("Attacking victim!");
                     }
                  }

                  player.swingArm(EnumHand.MAIN_HAND);
                  player.resetCooldown();
               }

            }
         }
      }
   }
}
