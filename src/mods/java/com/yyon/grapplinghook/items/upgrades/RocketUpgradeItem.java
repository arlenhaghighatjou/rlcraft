package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class RocketUpgradeItem extends BaseUpgradeItem {
   public RocketUpgradeItem() {
   }

   public void setvars() {
      this.unlocalizedname = "rocketupgradeitem";
      this.category = grapplemod.upgradeCategories.ROCKET;
   }
}
