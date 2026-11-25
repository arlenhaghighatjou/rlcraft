package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreAttributes;

public class PotionMagicInhibition extends PotionCorePotion {
   public static final String NAME = "magic_inhibition";
   public static final PotionMagicInhibition INSTANCE = new PotionMagicInhibition();
   public static float damageModifier = -0.3F;
   public static boolean addModifier = false;

   public PotionMagicInhibition() {
      super("magic_inhibition", true, 10845875);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(PotionCoreAttributes.MAGIC_DAMAGE, "3ffc62ff-aed2-4db2-a19e-cebe0c297648", (double)damageModifier, addModifier ? 0 : 2);
   }
}
