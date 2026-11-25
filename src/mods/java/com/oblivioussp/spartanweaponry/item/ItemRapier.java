package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;

public class ItemRapier extends ItemWeaponBase {
   public ItemRapier(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseRapier, ConfigHandler.damageMultiplierRapier, (double)ConfigHandler.speedRapier, WeaponProperties.DAMAGE_ABSORB_25, WeaponProperties.EXTRA_DAMAGE_3_NO_ARMOUR);
      this.displayName = "rapier_custom";
   }

   public ItemRapier(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemRapier(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }
}
