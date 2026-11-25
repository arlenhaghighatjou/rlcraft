package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.item.Item;

public class ItemGeneric extends Item {
   public ItemGeneric(String gameName, String name) {
      this.setCreativeTab(IceAndFire.TAB);
      this.setUnlocalizedName(name);
      this.setRegistryName("iceandfire", gameName);
   }
}
