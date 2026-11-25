package com.lycanitesmobs.core.item;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.ModInfo;

public class ItemCustom extends ItemBase {
   public ItemCustom(String itemName, ModInfo group) {
      this.itemName = itemName;
      this.modInfo = group;
      this.setCreativeTab(LycanitesMobs.itemsTab);
      this.setup();
   }
}
