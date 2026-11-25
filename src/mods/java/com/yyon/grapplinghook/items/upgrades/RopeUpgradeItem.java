package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class RopeUpgradeItem extends BaseUpgradeItem {
   public RopeUpgradeItem() {
   }

   public void setvars() {
      this.unlocalizedname = "ropeupgradeitem";
      this.category = grapplemod.upgradeCategories.ROPE;
   }
}
