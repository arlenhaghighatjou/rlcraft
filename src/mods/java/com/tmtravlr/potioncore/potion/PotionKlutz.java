package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreAttributes;

public class PotionKlutz extends PotionCorePotion {
   public static final String NAME = "klutz";
   public static final PotionKlutz INSTANCE = new PotionKlutz();
   public static float damageModifier = -4.0F;
   public static boolean addModifier = true;

   public PotionKlutz() {
      super("klutz", true, 10066227);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(PotionCoreAttributes.PROJECTILE_DAMAGE, "fd747754-0718-456c-8538-330c4ab65793", (double)damageModifier, addModifier ? 0 : 2);
   }
}
