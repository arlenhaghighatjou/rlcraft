package rlmixins.potion;

import net.minecraft.entity.EntityLivingBase;

public class PotionLesserFireResistance extends PotionBase {
   public static final PotionLesserFireResistance INSTANCE = new PotionLesserFireResistance();

   public PotionLesserFireResistance() {
      super("lesser_fire_resistance", false, 16544515);
   }

   public boolean isReady(int duration, int amplifier) {
      return false;
   }

   public void performEffect(EntityLivingBase entity, int amplifier) {
   }
}
