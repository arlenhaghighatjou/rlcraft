package com.oblivioussp.spartanshields.item;

import net.minecraft.item.ItemStack;

public interface IItemPoweredFE {
   int receiveFE(ItemStack var1, int var2, boolean var3);

   int extractFE(ItemStack var1, int var2, boolean var3);

   int getFEStored(ItemStack var1);

   int getFECapacity(ItemStack var1);

   boolean canExtractFE(ItemStack var1);

   boolean canReceiveFE(ItemStack var1);
}
