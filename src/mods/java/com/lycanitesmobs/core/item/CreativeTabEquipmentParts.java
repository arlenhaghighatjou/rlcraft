package com.lycanitesmobs.core.item;

import com.lycanitesmobs.ObjectManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabEquipmentParts extends CreativeTabs {
   public CreativeTabEquipmentParts(int tabID, String modID) {
      super(tabID, modID);
   }

   @SideOnly(Side.CLIENT)
   public ItemStack getTabIconItem() {
      if (ObjectManager.getItem("eechetikarm") != null) {
         return new ItemStack(ObjectManager.getItem("eechetikarm"));
      } else if (ObjectManager.getItem("darklingskull") != null) {
         return new ItemStack(ObjectManager.getItem("darklingskull"));
      } else if (ObjectManager.getItem("grueclaw") != null) {
         return new ItemStack(ObjectManager.getItem("grueclaw"));
      } else if (ObjectManager.getItem("xaphanspine") != null) {
         return new ItemStack(ObjectManager.getItem("xaphanspine"));
      } else if (ObjectManager.getItem("geonachfist") != null) {
         return new ItemStack(ObjectManager.getItem("geonachfist"));
      } else if (ObjectManager.getItem("HellfireCharge") != null) {
         return new ItemStack(ObjectManager.getItem("HellfireCharge"));
      } else if (ObjectManager.getItem("JoustMeat") != null) {
         return new ItemStack(ObjectManager.getItem("JoustMeat"));
      } else {
         return ObjectManager.getItem("PoisonGland") != null ? new ItemStack(ObjectManager.getItem("PoisonGland")) : new ItemStack(Items.BONE);
      }
   }
}
