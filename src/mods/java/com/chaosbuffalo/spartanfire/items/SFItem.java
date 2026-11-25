package com.chaosbuffalo.spartanfire.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class SFItem extends Item {
   public SFItem(ResourceLocation registryName, CreativeTabs tab) {
      this.setRegistryName(registryName);
      this.setUnlocalizedName(registryName.getResourcePath());
      this.setCreativeTab(tab);
   }
}
