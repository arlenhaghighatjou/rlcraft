package com.tmtravlr.potioncore.potion;

import net.minecraft.entity.player.EntityPlayer;

public class PotionReach extends PotionCorePotion {
   public static final String NAME = "reach";
   public static final String TAG_NAME = "potion core - reach";
   public static final PotionReach INSTANCE = new PotionReach();
   public static double rangeModifier = 1.0D;

   public PotionReach() {
      super("reach", false, 13120100);
   }

   public void registerPotionAttributeModifiers() {
      this.registerPotionAttributeModifier(EntityPlayer.REACH_DISTANCE, "3944821f-9c5b-41a3-8b4c-146447f591d3", rangeModifier, 0);
   }
}
