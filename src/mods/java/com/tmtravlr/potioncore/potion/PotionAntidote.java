package com.tmtravlr.potioncore.potion;

public class PotionAntidote extends PotionCorePotion {
   public static final String NAME = "antidote";
   public static final PotionAntidote INSTANCE = new PotionAntidote();

   public PotionAntidote() {
      super("antidote", false, 52377);
   }
}
