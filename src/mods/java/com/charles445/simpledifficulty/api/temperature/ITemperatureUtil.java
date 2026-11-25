package com.charles445.simpledifficulty.api.temperature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ITemperatureUtil {
   int getPlayerTargetTemperature(EntityPlayer var1);

   int getWorldTemperature(World var1, BlockPos var2);

   int clampTemperature(int var1);

   TemperatureEnum getTemperatureEnum(int var1);

   void setArmorTemperatureTag(ItemStack var1, float var2);

   float getArmorTemperatureTag(ItemStack var1);

   void removeArmorTemperatureTag(ItemStack var1);
}
