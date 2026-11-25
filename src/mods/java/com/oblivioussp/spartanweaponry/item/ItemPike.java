package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;

public class ItemPike extends ItemWeaponBase {
   public ItemPike(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBasePike, ConfigHandler.damageMultiplierPike, (double)ConfigHandler.speedPike, WeaponProperties.TWO_HANDED_1, WeaponProperties.REACH_2);
      this.displayName = "pike_custom";
   }

   public ItemPike(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemPike(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }
}
