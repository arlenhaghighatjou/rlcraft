package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.SharedMonsterAttributes;

public class PotionBrokenArmor extends PotionCorePotion {
   public static final String NAME = "broken_armor";
   public static final PotionBrokenArmor INSTANCE = new PotionBrokenArmor();
   public static float armorModifier = -0.25F;

   public PotionBrokenArmor() {
      super("broken_armor", true, 10583930);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR, "0c28991c-6d9f-43ad-bb4c-3eb886349406", (double)armorModifier, 2);
   }
}
