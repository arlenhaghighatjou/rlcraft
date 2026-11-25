package com.charles445.simpledifficulty.api.thirst;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IThirstUtil {
   @Nullable
   ThirstEnumBlockPos traceWater(EntityPlayer var1);

   void takeDrink(EntityPlayer var1, int var2, float var3, float var4);

   void takeDrink(EntityPlayer var1, int var2, float var3);

   void takeDrink(EntityPlayer var1, ThirstEnum var2);

   ItemStack createPurifiedWaterBucket();
}
