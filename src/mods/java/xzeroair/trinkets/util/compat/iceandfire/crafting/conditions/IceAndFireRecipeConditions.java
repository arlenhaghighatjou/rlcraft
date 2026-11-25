package xzeroair.trinkets.util.compat.iceandfire.crafting.conditions;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;

public class IceAndFireRecipeConditions implements IConditionFactory {
   public IceAndFireRecipeConditions() {
   }

   public BooleanSupplier parse(JsonContext context, JsonObject json) {
      boolean value = JsonUtils.getBoolean(json, "value", false);
      return () -> {
         return Loader.isModLoaded("iceandfire") == value;
      };
   }
}
