package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreAttributes;

public class PotionWeight extends PotionCorePotion {
   public static final String NAME = "weight";
   public static final PotionWeight INSTANCE = new PotionWeight();
   public static float heightReduction = -0.5F;

   public PotionWeight() {
      super("weight", true, 5592405);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(PotionCoreAttributes.JUMP_HEIGHT, "e4a96c88-9ac4-4b4b-afe5-fa13e4bcef6c", (double)heightReduction, 1);
   }
}
