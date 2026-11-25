package svenhjol.charm.crafting.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import svenhjol.charm.crafting.feature.SuspiciousSoup;

public class SuspiciousEffects {
   public SuspiciousEffects() {
   }

   public static void init() {
      SuspiciousSoup.effects.add(new SuspiciousEffects.CustomEffect() {
         public void performEffect(EntityLivingBase e, int duration, int amplifier) {
            this.doEffect(e, MobEffects.NIGHT_VISION, duration, amplifier);
            this.doSideEffect(e, MobEffects.WEAKNESS, 0.5F, duration, amplifier);
         }
      });
      SuspiciousSoup.effects.add(new SuspiciousEffects.CustomEffect() {
         public void performEffect(EntityLivingBase e, int duration, int amplifier) {
            this.doEffect(e, MobEffects.SPEED, duration, amplifier);
            this.doSideEffect(e, MobEffects.HUNGER, 0.5F, duration, amplifier);
         }
      });
      SuspiciousSoup.effects.add(new SuspiciousEffects.CustomEffect() {
         public void performEffect(EntityLivingBase e, int duration, int amplifier) {
            this.doEffect(e, MobEffects.SPEED, duration, amplifier * 2);
            this.doSideEffect(e, MobEffects.POISON, 0.5F, duration, amplifier);
         }
      });
      SuspiciousSoup.effects.add(new SuspiciousEffects.CustomEffect() {
         public void performEffect(EntityLivingBase e, int duration, int amplifier) {
            this.doEffect(e, MobEffects.REGENERATION, duration, amplifier);
            this.doSideEffect(e, MobEffects.SLOWNESS, 0.5F, duration, amplifier);
         }
      });
      SuspiciousSoup.effects.add(new SuspiciousEffects.CustomEffect() {
         public void performEffect(EntityLivingBase e, int duration, int amplifier) {
            this.doEffect(e, MobEffects.STRENGTH, duration, amplifier);
            this.doSideEffect(e, MobEffects.HUNGER, 0.5F, duration, amplifier);
         }
      });
      SuspiciousSoup.effects.add(new SuspiciousEffects.CustomEffect() {
         public void performEffect(EntityLivingBase e, int duration, int amplifier) {
            this.doEffect(e, MobEffects.STRENGTH, duration, amplifier * 2);
            this.doSideEffect(e, MobEffects.POISON, 0.5F, duration, amplifier);
         }
      });
      SuspiciousSoup.effects.add(new SuspiciousEffects.CustomEffect() {
         public void performEffect(EntityLivingBase e, int duration, int amplifier) {
            this.doEffect(e, MobEffects.JUMP_BOOST, duration, amplifier * 2);
            this.doSideEffect(e, MobEffects.HUNGER, 0.5F, duration, amplifier);
         }
      });
      SuspiciousSoup.effects.add(new SuspiciousEffects.CustomEffect() {
         public void performEffect(EntityLivingBase e, int duration, int amplifier) {
            this.doEffect(e, MobEffects.RESISTANCE, duration, amplifier);
            this.doSideEffect(e, MobEffects.SLOWNESS, 0.5F, duration, amplifier);
         }
      });
      SuspiciousSoup.effects.add(new SuspiciousEffects.CustomEffect() {
         public void performEffect(EntityLivingBase e, int duration, int amplifier) {
            int d = duration * (e.world.rand.nextInt(3) + 1);
            this.doEffect(e, MobEffects.WATER_BREATHING, d, amplifier);
            this.doSideEffect(e, MobEffects.SLOWNESS, 0.5F, d, amplifier);
         }
      });
      SuspiciousSoup.effects.add(new SuspiciousEffects.CustomEffect() {
         public void performEffect(EntityLivingBase e, int duration, int amplifier) {
            int d = duration * (e.world.rand.nextInt(3) + 1);
            this.doEffect(e, MobEffects.HASTE, d, amplifier);
            this.doSideEffect(e, MobEffects.BLINDNESS, 0.5F, d, amplifier);
         }
      });
      SuspiciousSoup.effects.add(new SuspiciousEffects.CustomEffect() {
         public void performEffect(EntityLivingBase e, int duration, int amplifier) {
            int d = duration * (e.world.rand.nextInt(3) + 1);
            this.doEffect(e, MobEffects.FIRE_RESISTANCE, d, amplifier);
            this.doSideEffect(e, MobEffects.SLOWNESS, 0.5F, d, amplifier);
         }
      });
      SuspiciousSoup.effects.add(new SuspiciousEffects.CustomEffect() {
         public void performEffect(EntityLivingBase e, int duration, int amplifier) {
            int d = duration * (e.world.rand.nextInt(3) + 1);
            this.doEffect(e, MobEffects.HEALTH_BOOST, d, amplifier);
            this.doSideEffect(e, MobEffects.HUNGER, 0.5F, d, amplifier);
         }
      });
      SuspiciousSoup.effects.add(new SuspiciousEffects.CustomEffect() {
         public void performEffect(EntityLivingBase e, int duration, int amplifier) {
            int d = duration * (e.world.rand.nextInt(3) + 1);
            this.doEffect(e, MobEffects.INVISIBILITY, d, amplifier);
            this.doSideEffect(e, MobEffects.HUNGER, 0.5F, d, amplifier * 2);
         }
      });
   }

   public static class CustomEffect {
      public CustomEffect() {
      }

      public void performEffect(EntityLivingBase e, int duration, int amplifier) {
      }

      public void doEffect(EntityLivingBase e, Potion potion, int duration, int amplifier) {
         e.addPotionEffect(new PotionEffect(potion, duration, amplifier));
      }

      public void doSideEffect(EntityLivingBase e, Potion potion, float chance, int duration, int amplifier) {
         if (e != null && e.world.rand.nextFloat() <= chance) {
            this.doEffect(e, potion, duration, amplifier);
         }

      }
   }
}
