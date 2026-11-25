package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class ThrowUpgradeItem extends BaseUpgradeItem {
   public ThrowUpgradeItem() {
   }

   public void setvars() {
      this.unlocalizedname = "throwupgradeitem";
      this.category = grapplemod.upgradeCategories.THROW;
   }
}
