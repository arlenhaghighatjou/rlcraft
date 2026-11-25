package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreAttributes;

public class PotionBrokenMagicShield extends PotionCorePotion {
   public static final String NAME = "broken_magic_shield";
   public static final PotionBrokenMagicShield INSTANCE = new PotionBrokenMagicShield();
   public static float magicShieldModifier = -0.25F;

   public PotionBrokenMagicShield() {
      super("broken_magic_shield", true, 11012960);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(PotionCoreAttributes.MAGIC_SHIELDING, "a6386f42-fc22-4d3b-bb43-0ab607738894", (double)magicShieldModifier, 2);
   }
}
