package org.jwaresoftware.mcmods.lib.recipes;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.JsonContext;
import org.jwaresoftware.mcmods.lib.api.mod.IModConfig;

public abstract class ConfigAllowsConditionFactoryBase extends ModConfigConditionFactoryImpl {
   protected ConfigAllowsConditionFactoryBase(@Nonnull IModConfig config) {
      super(config);
   }

   public final BooleanSupplier parse(JsonContext context, JsonObject json) {
      String feature = JsonUtils.getString(json, "value", "supercalifragilisticexpialidocious");
      boolean default_value = JsonUtils.getBoolean(json, "default_value", false);
      return () -> {
         return this._config.isAllowed(feature, default_value);
      };
   }
}
