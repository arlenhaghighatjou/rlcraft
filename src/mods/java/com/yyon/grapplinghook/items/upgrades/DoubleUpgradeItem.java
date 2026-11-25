package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class DoubleUpgradeItem extends BaseUpgradeItem {
   public DoubleUpgradeItem() {
   }

   public void setvars() {
      this.unlocalizedname = "doubleupgradeitem";
      this.category = grapplemod.upgradeCategories.DOUBLE;
   }
}
