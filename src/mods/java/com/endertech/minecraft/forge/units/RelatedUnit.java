package com.endertech.minecraft.forge.units;

import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.UnitConfig;

public class RelatedUnit implements IRelatedUnit {
   private final UnitConfig config;
   private final UnitId id;

   public RelatedUnit(UnitConfig config, UnitId id) {
      this.config = config;
      this.id = ForgeConfig.getUnitId(config, this.getClassCategory(), "id", id, "Id is a basic unit (block or item) identifier in <modId:unitName:meta> format.\nModId can be omitted for vanilla items. Meta can be omitted too if it equals 0.\nUnitName must be lowercase, words separated by '_', words order - from private to common (example: black_iron_ore).\nUnit name will be automatically converted to ore dictionary name with reverse word order (example: oreIronBlack).\nUse '*' char as meta value to specify all possible values.\nTo define multiple block states, you can use block properties instead of meta. The format is <modId:blockName:[prop1=value1, prop2=value2]>\nAlso you may use just ore dictionary name as full id.\n");
      this.saveConfig();
   }

   public ForgeConfig getConfig() {
      return this.config;
   }

   public UnitId getRelatedId() {
      return this.id;
   }
}
