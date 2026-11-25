package org.jwaresoftware.mcmods.lib.recipes;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import net.minecraftforge.common.crafting.JsonContext;
import org.jwaresoftware.mcmods.lib.api.mod.IModConfig;

public abstract class IsSupercompatibilityModeConditionFactoryBase extends ModConfigConditionFactoryImpl {
   protected IsSupercompatibilityModeConditionFactoryBase(@Nonnull IModConfig config) {
      super(config);
   }

   public final BooleanSupplier parse(JsonContext context, JsonObject json) {
      return () -> {
         return this._config.isSuperCompatibilityMode();
      };
   }
}
