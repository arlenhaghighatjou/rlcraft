package net.silentchaos512.lib.tile;

import net.minecraft.item.ItemStack;

public interface IFuelBurner {
   boolean feedFuel(ItemStack var1);

   boolean hasFuel();

   boolean tickFuel(boolean var1);
}
