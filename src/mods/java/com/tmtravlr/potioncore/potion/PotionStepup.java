package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreAttributes;

public class PotionStepup extends PotionCorePotion {
   public static final String NAME = "step_up";
   public static final String TAG_NAME = "potion core - step_up";
   public static final PotionStepup INSTANCE = new PotionStepup();
   public static float increase = 0.5F;

   public PotionStepup() {
      super("step_up", false, 3394611);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(PotionCoreAttributes.STEP_HEIGHT, "a8635510-c109-454b-8823-a73cbc406546", (double)increase, 0);
   }
}
