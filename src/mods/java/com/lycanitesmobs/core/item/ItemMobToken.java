package com.lycanitesmobs.core.item;

import com.lycanitesmobs.core.info.ModInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMobToken extends ItemBase {
   public ItemMobToken(ModInfo group) {
      this.itemName = "mobtoken";
      this.modInfo = group;
      this.setUnlocalizedName(this.itemName);
      this.setup();
   }

   public void setup() {
      this.setRegistryName(this.modInfo.modid, this.itemName);
      this.setUnlocalizedName(this.itemName);
   }

   @SideOnly(Side.CLIENT)
   public boolean isFull3D() {
      return true;
   }
}
