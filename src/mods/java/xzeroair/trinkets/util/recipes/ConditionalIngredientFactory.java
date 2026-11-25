package xzeroair.trinkets.util.recipes;

import com.google.gson.JsonObject;
import javax.annotation.Nonnull;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class ConditionalIngredientFactory implements IIngredientFactory {
   public ConditionalIngredientFactory() {
   }

   @Nonnull
   public Ingredient parse(JsonContext context, JsonObject json) {
      return (Ingredient)(CraftingHelper.processConditions(JsonUtils.getJsonArray(json, "conditions"), context) ? CraftingHelper.getIngredient(json.get("ingredient"), context) : IngredientNever.INSTANCE);
   }
}
