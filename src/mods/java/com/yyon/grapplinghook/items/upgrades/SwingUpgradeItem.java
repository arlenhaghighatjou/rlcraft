package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class SwingUpgradeItem extends BaseUpgradeItem {
   public SwingUpgradeItem() {
   }

   public void setvars() {
      this.unlocalizedname = "swingupgradeitem";
      this.category = grapplemod.upgradeCategories.SWING;
   }
}
