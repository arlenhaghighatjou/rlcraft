package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;

public class ItemSpear extends ItemWeaponBase {
   public ItemSpear(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseSpear, ConfigHandler.damageMultiplierSpear, (double)ConfigHandler.speedSpear, WeaponProperties.REACH_1);
      this.displayName = "spear_custom";
   }

   public ItemSpear(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemSpear(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }
}
