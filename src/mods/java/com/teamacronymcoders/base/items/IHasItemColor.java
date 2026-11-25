package com.teamacronymcoders.base.items;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public interface IHasItemColor {
   int getColorFromItemstack(@Nonnull ItemStack var1, int var2);
}
