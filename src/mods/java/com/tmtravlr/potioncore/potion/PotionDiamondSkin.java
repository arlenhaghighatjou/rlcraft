package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.SharedMonsterAttributes;

public class PotionDiamondSkin extends PotionCorePotion {
   public static final String NAME = "diamond_skin";
   public static final PotionDiamondSkin INSTANCE = new PotionDiamondSkin();
   public static float armorModifier = 4.0F;

   public PotionDiamondSkin() {
      super("diamond_skin", false, 2873584);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR_TOUGHNESS, "2ea81026-04ac-4258-885f-b0e6a2cdc3b8", (double)armorModifier, 0);
   }
}
