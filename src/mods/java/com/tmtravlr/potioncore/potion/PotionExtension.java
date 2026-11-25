package com.tmtravlr.potioncore.potion;

import java.util.ArrayList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

public class PotionExtension extends PotionCorePotion {
   public static final String NAME = "extension";
   public static final PotionExtension INSTANCE = new PotionExtension();

   public PotionExtension() {
      super("extension", false, 10027161);
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      ArrayList<PotionEffect> potionList = new ArrayList(entity.getActivePotionEffects());
      potionList.remove(entity.getActivePotionEffect(this));
      int i = amplifier + 1;

      while(potionList.size() > 0 && i-- > 0) {
         PotionEffect effect = (PotionEffect)potionList.remove(entity.getRNG().nextInt(potionList.size()));
         if (effect.getPotion() != this && !effect.getPotion().isInstant()) {
            effect.combine(new PotionEffect(effect.getPotion(), effect.getDuration() + 1, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
         }
      }

   }
}
