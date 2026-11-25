package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.SharedMonsterAttributes;

public class PotionSolidCore extends PotionCorePotion {
   public static final String NAME = "solid_core";
   public static final PotionSolidCore INSTANCE = new PotionSolidCore();

   public PotionSolidCore() {
      super("solid_core", false, 2236962);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, "9bface7b-f0d0-4bdb-9c0c-09c3237fa99c", 1.0D, 0);
   }
}
