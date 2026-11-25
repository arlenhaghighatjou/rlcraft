package com.yungnickyoung.minecraft.yungslaw.config.util;

import com.yungnickyoung.minecraft.yungslaw.config.Configuration;
import java.util.HashMap;
import java.util.Map;

public class ConfigHolder {
   public Map<String, ConfigOption<?>> properties = new HashMap();
   public ConfigOption<Integer> genDistance;
   public ConfigOption<String> hardBlock;
   public ConfigOption<String[]> safeBlocks;
   public ConfigOption<String[]> untouchableBlocks;
   public ConfigOption<Integer> maxAltitude;
   public ConfigOption<Boolean> enableLiquidSafety;
   public ConfigOption<Boolean> enableOreDeletion;
   public ConfigOption<String[]> oreWhitelist;

   public ConfigHolder() {
      this.genDistance = (new ConfigOption<Integer>("Generation Distance", Configuration.replacementSettings.genDistance)).setCategory("general.Replacement Settings").addToMap(this.properties);
      this.hardBlock = (new ConfigOption<String>("Hard Block", Configuration.replacementSettings.replacementMode.hardBlock)).setCategory("general.Replacement Settings.Replacement Mode Settings").addToMap(this.properties);
      this.safeBlocks = (new ConfigOption<String[]>("Safe Blocks", Configuration.replacementSettings.safeBlocks)).setCategory("general.Replacement Settings").addToMap(this.properties);
      this.untouchableBlocks = (new ConfigOption<String[]>("Untouchable Blocks", Configuration.replacementSettings.untouchableBlocks)).setCategory("general.Replacement Settings").addToMap(this.properties);
      this.maxAltitude = (new ConfigOption<Integer>("Max Altitude", Configuration.replacementSettings.maxAltitude)).setCategory("general.Replacement Settings").addToMap(this.properties);
      this.enableLiquidSafety = (new ConfigOption<Boolean>("Mark Liquids As Safe", Configuration.replacementSettings.enableLiquidSafety)).setCategory("general.Replacement Settings").addToMap(this.properties);
      this.enableOreDeletion = (new ConfigOption<Boolean>("Enable Ore Deletion Mode", Configuration.replacementSettings.enableOreDeletion)).setCategory("general.Replacement Settings").addToMap(this.properties);
      this.oreWhitelist = (new ConfigOption<String[]>("Ore Block Whitelist", Configuration.replacementSettings.oreMode.oreWhitelist)).setCategory("general.Replacement Settings.Ore Deletion Mode Settings").addToMap(this.properties);
   }
}
