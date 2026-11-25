package com.tmtravlr.potioncore.potion;

public class PotionRecoil extends PotionCorePotion {
   public static final String NAME = "recoil";
   public static final PotionRecoil INSTANCE = new PotionRecoil();
   public static float reflectDamage = 0.1F;

   public PotionRecoil() {
      super("recoil", false, 34850);
   }
}
