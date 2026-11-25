package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;

public class ItemLance extends ItemSwordBase {
   public ItemLance(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseLance, ConfigHandler.damageMultiplierLance, (double)ConfigHandler.speedLance, WeaponProperties.REACH_1, WeaponProperties.EXTRA_DAMAGE_2_RIDING);
      this.displayName = "lance_custom";
   }

   public ItemLance(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemLance(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }
}
