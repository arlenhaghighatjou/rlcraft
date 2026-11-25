package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class ForcefieldUpgradeItem extends BaseUpgradeItem {
   public ForcefieldUpgradeItem() {
   }

   public void setvars() {
      this.unlocalizedname = "forcefieldupgradeitem";
      this.category = grapplemod.upgradeCategories.FORCEFIELD;
   }
}
