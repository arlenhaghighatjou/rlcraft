package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;

public class ItemMace extends ItemWeaponBase {
   public ItemMace(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseMace, ConfigHandler.damageMultiplierMace, (double)ConfigHandler.speedMace, WeaponProperties.EXTRA_DAMAGE_50P_UNDEAD);
      this.displayName = "mace_custom";
   }

   public ItemMace(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemMace(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }
}
