package com.charles445.simpledifficulty.api.item;

import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;

public interface IItemCanteen {
   @Nullable
   ThirstEnum getThirstEnum(ItemStack var1);

   void setDoses(ItemStack var1, ThirstEnum var2, int var3);

   void setDoses(ItemStack var1, int var2);

   boolean tryAddDose(ItemStack var1, ThirstEnum var2);

   void removeDose(ItemStack var1);

   void setCanteenFull(ItemStack var1);

   void setCanteenEmpty(ItemStack var1);

   boolean isCanteenFull(ItemStack var1);

   boolean isCanteenEmpty(ItemStack var1);

   int getMaxDoses(ItemStack var1);

   int getDoses(ItemStack var1);
}
