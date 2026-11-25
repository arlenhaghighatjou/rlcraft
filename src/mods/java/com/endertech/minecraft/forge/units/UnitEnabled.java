package com.endertech.minecraft.forge.units;

import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.UnitConfig;

public class UnitEnabled {
   private final boolean enabled;

   public UnitEnabled(UnitConfig config, String category, boolean defaultValue) {
      this.enabled = ForgeConfig.getBool(config, category, "enabled", defaultValue, "Set to 'false' if you don't need this unit.");
   }

   public UnitEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isEnabled() {
      return this.enabled;
   }
}
