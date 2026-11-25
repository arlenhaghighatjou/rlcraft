package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;

public class ItemHammer extends ItemWeaponBase {
   public ItemHammer(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseHammer, ConfigHandler.damageMultiplierHammer, (double)ConfigHandler.speedHammer, WeaponProperties.KNOCKBACK, WeaponProperties.NAUSEA);
      this.displayName = "hammer_custom";
   }

   public ItemHammer(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemHammer(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }
}
