package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;

public class ItemWarhammer extends ItemWeaponBase {
   public ItemWarhammer(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseWarhammer, ConfigHandler.damageMultiplierWarhammer, (double)ConfigHandler.speedWarhammer, WeaponProperties.TWO_HANDED_1, WeaponProperties.ARMOUR_PIERCING_50);
      this.displayName = "warhammer_custom";
   }

   public ItemWarhammer(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemWarhammer(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }
}
