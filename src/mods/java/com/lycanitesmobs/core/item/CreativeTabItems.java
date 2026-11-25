package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabItems extends CreativeTabs {
   public CreativeTabItems(int tabID, String modID) {
      super(tabID, modID);
   }

   @SideOnly(Side.CLIENT)
   public ItemStack getTabIconItem() {
      if (ObjectManager.getItem("soulgazer") != null) {
         return new ItemStack(ObjectManager.getItem("soulgazer"));
      } else {
         return ObjectManager.getItem("poisongland") != null ? new ItemStack(ObjectManager.getItem("poisongland")) : new ItemStack(Items.EMERALD);
      }
   }
}
