package snownee.kiwi.crafting;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import snownee.kiwi.Kiwi;

public class ConditionModuleLoaded implements IConditionFactory {
   public ConditionModuleLoaded() {
   }

   public BooleanSupplier parse(JsonContext context, JsonObject json) {
      ResourceLocation module = new ResourceLocation(JsonUtils.getString(json, "module"));
      return () -> {
         return Kiwi.isLoaded(module);
      };
   }
}
