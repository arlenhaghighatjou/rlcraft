package com.yyon.grapplinghook.items.upgrades;

import com.yyon.grapplinghook.grapplemod;

public class MotorUpgradeItem extends BaseUpgradeItem {
   public MotorUpgradeItem() {
   }

   public void setvars() {
      this.unlocalizedname = "motorupgradeitem";
      this.category = grapplemod.upgradeCategories.MOTOR;
   }
}
