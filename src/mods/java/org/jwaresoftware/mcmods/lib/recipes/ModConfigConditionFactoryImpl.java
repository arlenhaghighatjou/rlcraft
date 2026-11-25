package org.jwaresoftware.mcmods.lib.recipes;

import javax.annotation.Nonnull;
import net.minecraftforge.common.crafting.IConditionFactory;
import org.jwaresoftware.mcmods.lib.api.mod.IModConfig;

public abstract class ModConfigConditionFactoryImpl implements IConditionFactory {
   protected final IModConfig _config;

   protected ModConfigConditionFactoryImpl(@Nonnull IModConfig config) {
      this._config = config;
   }
}
