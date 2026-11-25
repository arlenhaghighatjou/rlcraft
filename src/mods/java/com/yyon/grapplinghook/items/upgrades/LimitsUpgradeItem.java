package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class LimitsUpgradeItem extends BaseUpgradeItem {
   public LimitsUpgradeItem() {
   }

   public void setvars() {
      this.unlocalizedname = "limitsupgradeitem";
      this.category = grapplemod.upgradeCategories.LIMITS;
   }
}
