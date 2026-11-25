package org.jwaresoftware.mcmods.armorunder.recipes;

import org.jwaresoftware.mcmods.armorunder.ModConfig;
import org.jwaresoftware.mcmods.lib.recipes.IsSupercompatibilityModeConditionFactoryBase;

public final class IsSupercompatibilityModeConditionFactory extends IsSupercompatibilityModeConditionFactoryBase {
   public IsSupercompatibilityModeConditionFactory() {
      super(ModConfig.getInstance());
   }
}
