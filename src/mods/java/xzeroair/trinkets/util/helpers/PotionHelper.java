package xzeroair.trinkets.util.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.Loader;

public class PotionHelper {
   public PotionHelper() {
   }

   public static PotionHelper.PotionHolder getPotionHolder(String potConfig) {
      return new PotionHelper.PotionHolder(potConfig);
   }

   public static boolean isModPotionActive(EntityLivingBase entity, String modID, String potionEffect) {
      Potion potion = getModPotion(modID, potionEffect);
      return potion != null && entity.isPotionActive(potion);
   }

   public static Potion getModPotion(String modID, String potionEffect) {
      if (Loader.isModLoaded(modID)) {
         try {
            return Potion.getPotionFromResourceLocation(potionEffect);
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

      return null;
   }

   public static class PotionHolder {
      Potion potion;
      PotionEffect effect;
      int duration;
      int amplifier;

      public PotionHolder(String configPot) {
         this.getPotionEffect(configPot);
      }

      public Potion getPotion() {
         return this.potion;
      }

      public PotionEffect getPotionEffect() {
         return this.effect;
      }

      public int getDuration() {
         return this.duration;
      }

      public int getAmplifier() {
         return this.amplifier;
      }

      private void getPotionEffect(String potID) {
         if (!potID.isEmpty()) {
            String pot = "";
            String[] configString = potID.split(":");
            String modID = configString[0];
            if (configString.length > 1) {
               String effectID = configString[1];
               pot = modID + ":" + effectID;
            } else {
               pot = "minecraft:" + modID;
            }

            Potion potion = Potion.getPotionFromResourceLocation(pot);
            if (potion != null) {
               int duration = 300;
               int amplifier = 0;
               if (configString.length > 2) {
                  try {
                     String durationString = configString[2];
                     duration = Integer.parseInt(durationString);
                  } catch (Exception var10) {
                     ;
                  }

                  if (configString.length > 3) {
                     try {
                        String amplifierString = configString[3];
                        amplifier = Integer.parseInt(amplifierString);
                     } catch (Exception var9) {
                        ;
                     }
                  }
               }

               this.potion = potion;
               this.duration = duration;
               this.amplifier = amplifier;
               this.effect = new PotionEffect(potion, duration, amplifier, false, false);
            }
         }

      }
   }
}
