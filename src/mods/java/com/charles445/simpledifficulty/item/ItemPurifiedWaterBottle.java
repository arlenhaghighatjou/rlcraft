package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import net.minecraft.item.ItemStack;

public class ItemPurifiedWaterBottle extends ItemDrinkBase {
   public ItemPurifiedWaterBottle() {
   }

   public int getThirstLevel(ItemStack stack) {
      return ThirstEnum.PURIFIED.getThirst();
   }

   public float getSaturationLevel(ItemStack stack) {
      return ThirstEnum.PURIFIED.getSaturation();
   }

   public float getDirtyChance(ItemStack stack) {
      return ThirstEnum.PURIFIED.getThirstyChance();
   }
}
