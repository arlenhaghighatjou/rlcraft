package com.oblivioussp.spartanweaponry.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IHudLoadState {
   boolean isLoaded(ItemStack var1);

   float getLoadProgress(ItemStack var1, EntityLivingBase var2);
}
