package com.oblivioussp.spartanshields.util;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;

public class ConditionFactoryModId implements IConditionFactory {
   public ConditionFactoryModId() {
   }

   public BooleanSupplier parse(JsonContext context, JsonObject json) {
      String modId = json.get("mod").getAsString();
      boolean result = Loader.isModLoaded(modId) && !ConfigHandler.vanillaOnly;
      return () -> {
         return result;
      };
   }
}
