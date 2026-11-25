package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreEffects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionInvert extends PotionCorePotion {
   public static final String NAME = "invert";
   public static final PotionInvert INSTANCE = new PotionInvert();

   public PotionInvert() {
      super("invert", true, 10092441);
   }

   public boolean isInstant() {
      return true;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
      if (!entity.world.isRemote) {
         entity.world.getMinecraftServer().addScheduledTask(() -> {
            invertPotionEffects(entity);
         });
      }

   }

   private static void invertPotionEffects(EntityLivingBase entity) {
      PotionEffect[] effects = new PotionEffect[0];
      effects = (PotionEffect[])entity.getActivePotionEffects().toArray(effects);

      for(int i = 0; i < effects.length; ++i) {
         PotionEffect effect = effects[i];
         if (effect != null && effect.getPotion() != null && PotionCoreEffects.OPPOSITE_EFFECTS.containsKey(effect.getPotion())) {
            Potion potion = (Potion)PotionCoreEffects.OPPOSITE_EFFECTS.get(effect.getPotion());
            int duration = effect.getDuration();
            if (potion.isInstant()) {
               duration = 1;
            }

            entity.removePotionEffect(effect.getPotion());
            entity.addPotionEffect(new PotionEffect(potion, duration, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
         }
      }

   }
}
