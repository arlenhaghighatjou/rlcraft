package com.teammetallurgy.aquaculture.handlers;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class AquacultureTab extends CreativeTabs {
   private ItemStack tabItem;

   public AquacultureTab(String label) {
      super(label);
   }

   public void setItemStack(@Nonnull ItemStack itemStack) {
      this.tabItem = itemStack;
   }

   @Nonnull
   public ItemStack getTabIconItem() {
      return this.tabItem;
   }
}
