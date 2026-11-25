package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreAttributes;

public class PotionMagicFocus extends PotionCorePotion {
   public static final String NAME = "magic_focus";
   public static final PotionMagicFocus INSTANCE = new PotionMagicFocus();
   public static float damageModifier = 0.75F;
   public static boolean addModifier = false;

   public PotionMagicFocus() {
      super("magic_focus", false, 15114239);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(PotionCoreAttributes.MAGIC_DAMAGE, "2da2fa36-cbe5-4211-8316-51e4d687ef2d", (double)damageModifier, addModifier ? 0 : 2);
   }
}
