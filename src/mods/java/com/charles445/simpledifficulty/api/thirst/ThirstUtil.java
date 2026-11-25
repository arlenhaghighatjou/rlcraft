package com.charles445.simpledifficulty.api.thirst;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ThirstUtil {
   public static IThirstUtil internal;

   public ThirstUtil() {
   }

   @Nullable
   public static ThirstEnumBlockPos traceWater(EntityPlayer player) {
      return internal.traceWater(player);
   }

   public static void takeDrink(EntityPlayer player, int thirst, float saturation, float thirstyChance) {
      internal.takeDrink(player, thirst, saturation, thirstyChance);
   }

   public static void takeDrink(EntityPlayer player, int thirst, float saturation) {
      internal.takeDrink(player, thirst, saturation);
   }

   public static void takeDrink(EntityPlayer player, ThirstEnum thirstEnum) {
      internal.takeDrink(player, thirstEnum);
   }

   public static ItemStack createPurifiedWaterBucket() {
      return internal.createPurifiedWaterBucket();
   }
}
