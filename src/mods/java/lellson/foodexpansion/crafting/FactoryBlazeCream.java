package lellson.foodexpansion.crafting;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import lellson.foodexpansion.FoodExpansionConfig;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class FactoryBlazeCream implements IConditionFactory {
   public FactoryBlazeCream() {
   }

   public BooleanSupplier parse(JsonContext context, JsonObject json) {
      return () -> {
         return !FoodExpansionConfig.disableRecipeBlazeCream;
      };
   }
}
