package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class StaffUpgradeItem extends BaseUpgradeItem {
   public StaffUpgradeItem() {
   }

   public void setvars() {
      this.unlocalizedname = "staffupgradeitem";
      this.category = grapplemod.upgradeCategories.STAFF;
   }
}
