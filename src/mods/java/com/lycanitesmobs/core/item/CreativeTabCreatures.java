package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabCreatures extends CreativeTabs {
   public CreativeTabCreatures(int tabID, String modID) {
      super(tabID, modID);
   }

   @SideOnly(Side.CLIENT)
   public ItemStack getTabIconItem() {
      if (ObjectManager.getItem("beastspawn") != null) {
         return new ItemStack(ObjectManager.getItem("beastspawn"));
      } else if (ObjectManager.getItem("demonspawn") != null) {
         return new ItemStack(ObjectManager.getItem("demonspawn"));
      } else if (ObjectManager.getItem("avianspawn") != null) {
         return new ItemStack(ObjectManager.getItem("avianspawn"));
      } else {
         return ObjectManager.getItem("arthropodspawn") != null ? new ItemStack(ObjectManager.getItem("arthropodspawn")) : new ItemStack(Items.SPAWN_EGG);
      }
   }
}
