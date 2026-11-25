package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;

public class ItemHalberd extends ItemWeaponBase {
   public ItemHalberd(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseHalberd, ConfigHandler.damageMultiplierHalberd, (double)ConfigHandler.speedHalberd, WeaponProperties.TWO_HANDED_2, WeaponProperties.REACH_1, WeaponProperties.SHIELD_BREACH);
      this.displayName = "halberd_custom";
   }

   public ItemHalberd(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemHalberd(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }
}
