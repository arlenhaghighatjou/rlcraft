package com.tmtravlr.potioncore.potion;

import com.tmtravlr.potioncore.PotionCoreAttributes;

public class PotionArchery extends PotionCorePotion {
   public static final String NAME = "archery";
   public static final PotionArchery INSTANCE = new PotionArchery();
   public static float damageModifier = 3.0F;
   public static boolean addModifier = true;

   public PotionArchery() {
      super("archery", false, 10048768);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(PotionCoreAttributes.PROJECTILE_DAMAGE, "ad83c3cc-e133-41f2-bd3c-c553902f7eca", (double)damageModifier, addModifier ? 0 : 2);
   }
}
