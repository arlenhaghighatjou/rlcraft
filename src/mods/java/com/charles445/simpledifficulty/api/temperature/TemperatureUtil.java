package com.charles445.simpledifficulty.api.temperature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TemperatureUtil {
   public static ITemperatureUtil internal;

   public TemperatureUtil() {
   }

   public static int getPlayerTargetTemperature(EntityPlayer player) {
      return internal.getPlayerTargetTemperature(player);
   }

   public static int getWorldTemperature(World world, BlockPos pos) {
      return internal.getWorldTemperature(world, pos);
   }

   public static int clampTemperature(int temperature) {
      return internal.clampTemperature(temperature);
   }

   public static TemperatureEnum getTemperatureEnum(int temperature) {
      return internal.getTemperatureEnum(temperature);
   }

   public static void setArmorTemperatureTag(ItemStack stack, float temperature) {
      internal.setArmorTemperatureTag(stack, temperature);
   }

   public static float getArmorTemperatureTag(ItemStack stack) {
      return internal.getArmorTemperatureTag(stack);
   }

   public static void removeArmorTemperatureTag(ItemStack stack) {
      internal.removeArmorTemperatureTag(stack);
   }
}
