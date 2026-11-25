package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;
import net.minecraft.item.Item;

public class BaseUpgradeItem extends Item {
   public String unlocalizedname;
   public grapplemod.upgradeCategories category;

   public BaseUpgradeItem() {
      this.maxStackSize = 1;
      this.setvars();
      this.setFull3D();
      this.setUnlocalizedName(this.unlocalizedname);
      this.setCreativeTab(grapplemod.tabGrapplemod);
   }

   public void setvars() {
      this.unlocalizedname = "baseupgradeitem";
      this.category = null;
      this.maxStackSize = 64;
   }
}
