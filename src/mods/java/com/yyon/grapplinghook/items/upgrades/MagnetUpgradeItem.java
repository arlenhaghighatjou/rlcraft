package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class MagnetUpgradeItem extends BaseUpgradeItem {
   public MagnetUpgradeItem() {
   }

   public void setvars() {
      this.unlocalizedname = "magnetupgradeitem";
      this.category = grapplemod.upgradeCategories.MAGNET;
   }
}
