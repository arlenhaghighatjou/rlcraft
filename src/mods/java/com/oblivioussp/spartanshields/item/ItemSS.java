package com.oblivioussp.spartanshields.item;

import com.oblivioussp.spartanshields.client.gui.CreativeTabsSS;
import net.minecraft.item.Item;

public class ItemSS extends Item {
   public ItemSS(String unlocName) {
      this.setCreativeTab(CreativeTabsSS.TAB_SS);
      this.setRegistryName(unlocName);
      this.setUnlocalizedName(unlocName);
   }
}
