package com.charles445.rltweaker.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionCoreUtil {
   public PotionCoreUtil() {
   }

   public static void scheduleRemovePotion(EntityLivingBase entity, Potion potion) {
      if (entity.isPotionActive(potion) && entity.getActivePotionEffect(potion).getDuration() > 1) {
         entity.removePotionEffect(potion);
         entity.addPotionEffect(new PotionEffect(potion, 1));
      }

   }
}
