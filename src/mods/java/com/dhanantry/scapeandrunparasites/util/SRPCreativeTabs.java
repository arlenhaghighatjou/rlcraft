package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public final class SRPCreativeTabs extends CreativeTabs {
   public SRPCreativeTabs(String label) {
      super(label);
   }

   public ItemStack getTabIconItem() {
      return new ItemStack(SRPItems.itembase);
   }
}
