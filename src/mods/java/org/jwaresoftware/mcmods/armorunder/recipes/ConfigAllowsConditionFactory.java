package org.jwaresoftware.mcmods.armorunder.recipes;

import org.jwaresoftware.mcmods.armorunder.ModConfig;
import org.jwaresoftware.mcmods.lib.recipes.ConfigAllowsConditionFactoryBase;

public final class ConfigAllowsConditionFactory extends ConfigAllowsConditionFactoryBase {
   public ConfigAllowsConditionFactory() {
      super(ModConfig.getInstance());
   }
}
