package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;

public class PotionTeleport extends PotionCorePotion {
   public static final String NAME = "teleport";
   public static final PotionTeleport INSTANCE = new PotionTeleport();
   public static float teleportRange = 100.0F;

   public PotionTeleport() {
      super("teleport", true, 52377);
   }

   public boolean isInstant() {
      return true;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      int tries = 20;
      double posX = entity.posX;
      double posY = entity.posY;
      double posZ = entity.posZ;

      boolean success;
      for(success = false; tries > 0 && !success; --tries) {
         double d0 = entity.posX + (entity.getRNG().nextDouble() - 0.5D) * (double)teleportRange * (double)(amplifier + 1);
         double d1 = entity.posY + (double)(entity.getRNG().nextInt(64) - 32);
         double d2 = entity.posZ + (entity.getRNG().nextDouble() - 0.5D) * (double)teleportRange * (double)(amplifier + 1);
         success = entity.attemptTeleport(d0, d1, d2);
         if (success) {
            entity.world.playSound((EntityPlayer)null, posX, posY, posZ, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);
            entity.world.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);
            break;
         }
      }

      if (!success) {
         entity.attemptTeleport(posX, posY, posZ);
      }

   }
}
