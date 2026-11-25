package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreAttributes;

public class PotionMagicShield extends PotionCorePotion {
   public static final String NAME = "magic_shield";
   public static final PotionMagicShield INSTANCE = new PotionMagicShield();
   public static float armorModifier = 4.0F;

   public PotionMagicShield() {
      super("magic_shield", false, 16729770);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(PotionCoreAttributes.MAGIC_SHIELDING, "6a64d921-fa98-45da-b8da-67e82f7ceafd", (double)armorModifier, 0);
   }
}
