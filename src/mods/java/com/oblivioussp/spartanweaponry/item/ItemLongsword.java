package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;

public class ItemLongsword extends ItemSwordBase {
   public ItemLongsword(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseLongsword, ConfigHandler.damageMultiplierLongsword, (double)ConfigHandler.speedLongsword, WeaponProperties.SWEEP_DAMAGE_NORMAL, WeaponProperties.TWO_HANDED_1);
      this.displayName = "longsword_custom";
   }

   public ItemLongsword(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemLongsword(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }
}
