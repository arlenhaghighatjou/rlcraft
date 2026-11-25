package com.tmtravlr.potioncore.potion;

public class PotionPurity extends PotionCorePotion {
   public static final String NAME = "purity";
   public static final PotionPurity INSTANCE = new PotionPurity();

   public PotionPurity() {
      super("purity", false, 16777215);
   }
}
