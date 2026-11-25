package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.SharedMonsterAttributes;

public class PotionIronSkin extends PotionCorePotion {
   public static final String NAME = "iron_skin";
   public static final PotionIronSkin INSTANCE = new PotionIronSkin();
   public static float armorModifier = 2.0F;

   public PotionIronSkin() {
      super("iron_skin", false, 13092807);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR, "f9c58d75-afcf-43ce-956e-4fb794356ec2", (double)armorModifier, 0);
   }
}
