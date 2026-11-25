package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;

public class ItemSaber extends ItemSwordBase {
   public ItemSaber(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseSaber, ConfigHandler.damageMultiplierSaber, (double)ConfigHandler.speedSaber, WeaponProperties.DAMAGE_ABSORB_25, WeaponProperties.EXTRA_DAMAGE_2_CHEST, WeaponProperties.SWEEP_DAMAGE_NORMAL);
      this.displayName = "saber_custom";
   }

   public ItemSaber(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemSaber(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }
}
