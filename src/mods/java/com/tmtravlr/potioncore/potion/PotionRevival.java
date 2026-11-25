package com.tmtravlr.potioncore.potion;

public class PotionRevival extends PotionCorePotion {
   public static final String NAME = "revival";
   public static final PotionRevival INSTANCE = new PotionRevival();
   public static float reviveHealth = 4.0F;

   public PotionRevival() {
      super("revival", false, 16711680);
   }
}
