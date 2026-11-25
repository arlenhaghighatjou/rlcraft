package com.mcwbridges.kikoz.tabs;

import com.mcwbridges.kikoz.init.BlockInit;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class MacawBridgesTab extends CreativeTabs {
   public MacawBridgesTab(String label) {
      super("macawbridgestab");
      this.setBackgroundImageName("macawbridgestab.png");
   }

   public ItemStack getTabIconItem() {
      return new ItemStack(BlockInit.MOST2);
   }
}
