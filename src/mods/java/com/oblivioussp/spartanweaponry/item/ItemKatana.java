package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;

public class ItemKatana extends ItemSwordBase {
   public ItemKatana(String unlocName, ToolMaterialEx material) {
      super(unlocName, material, ConfigHandler.damageBaseKatana, ConfigHandler.damageMultiplierKatana, (double)ConfigHandler.speedKatana, WeaponProperties.SWEEP_DAMAGE_NORMAL, WeaponProperties.TWO_HANDED_1, WeaponProperties.EXTRA_DAMAGE_2_CHEST);
      this.displayName = "katana_custom";
   }

   public ItemKatana(String unlocName, String externalModId, ToolMaterialEx material) {
      this(unlocName, material);
      this.modId = externalModId;
   }

   /** @deprecated */
   @Deprecated
   public ItemKatana(String unlocName, String externalModId, ToolMaterialEx material, float weaponDamage) {
      this(unlocName, externalModId, material);
   }
}
