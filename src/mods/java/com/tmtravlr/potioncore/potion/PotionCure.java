package com.tmtravlr.potioncore.potion;

import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionCure extends PotionCorePotion {
   public static final String NAME = "cure";
   public static final PotionCure INSTANCE = new PotionCure();

   public PotionCure() {
      super("cure", false, 16733695);
   }

   public boolean isInstant() {
      return true;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (!entity.world.isRemote) {
         entity.world.getMinecraftServer().addScheduledTask(() -> {
            this.clearNegativeEffects(entity);
         });
      }

   }

   private void clearNegativeEffects(EntityLivingBase entity) {
      Collection<PotionEffect> effects = entity.getActivePotionEffects();
      ArrayList<Potion> potionsToRemove = new ArrayList();

      for(PotionEffect effect : effects) {
         if (effect.getPotion().isBadEffect()) {
            potionsToRemove.add(effect.getPotion());
         }
      }

      for(Potion potion : potionsToRemove) {
         entity.removePotionEffect(potion);
      }

   }
}
