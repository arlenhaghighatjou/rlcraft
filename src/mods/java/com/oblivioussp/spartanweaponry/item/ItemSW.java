package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.client.gui.CreativeTabsSW;
import com.oblivioussp.spartanweaponry.util.StringHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemSW extends Item {
   public ItemSW(String unlocName) {
      this.setCreativeTab(CreativeTabsSW.TAB_SW);
      this.setRegistryName(unlocName);
      this.setUnlocalizedName(unlocName);
   }

   public String getUnlocalizedName() {
      return StringHelper.getItemUnlocalizedName(super.getUnlocalizedName());
   }

   public String getUnlocalizedName(ItemStack itemStack) {
      return StringHelper.getItemUnlocalizedName(super.getUnlocalizedName());
   }
}
