package org.orecruncher.lib;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.PotionEffect;

public final class EntityUtils {
   private EntityUtils() {
   }

   public static boolean hasNegativePotionEffects(@Nonnull EntityLiving entity) {
      for(PotionEffect effect : entity.getActivePotionEffects()) {
         if (effect.getPotion().isBadEffect()) {
            return true;
         }
      }

      return false;
   }
}
