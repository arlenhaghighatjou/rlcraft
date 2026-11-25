package com.tmtravlr.potioncore.potion;

import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionDispel extends PotionCorePotion {
   public static final String NAME = "dispel";
   public static final PotionDispel INSTANCE = new PotionDispel();

   public PotionDispel() {
      super("dispel", true, 10027161);
   }

   public boolean isInstant() {
      return true;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (!entity.world.isRemote) {
         entity.world.getMinecraftServer().addScheduledTask(() -> {
            this.clearPositiveEffects(entity);
         });
      }

   }

   private void clearPositiveEffects(EntityLivingBase entity) {
      Collection<PotionEffect> effects = entity.getActivePotionEffects();
      ArrayList<Potion> potionsToRemove = new ArrayList();

      for(PotionEffect effect : effects) {
         if (!effect.getPotion().isBadEffect()) {
            potionsToRemove.add(effect.getPotion());
         }
      }

      for(Potion potion : potionsToRemove) {
         entity.removePotionEffect(potion);
      }

   }
}
