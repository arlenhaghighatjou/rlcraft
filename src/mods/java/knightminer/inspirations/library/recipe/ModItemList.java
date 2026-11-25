package knightminer.inspirations.library.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;

public class ModItemList extends CompoundIngredient {
   protected ModItemList(Collection<Ingredient> children) {
      super(children);
   }

   public static class Factory implements IIngredientFactory {
      public Factory() {
      }

      public Ingredient parse(JsonContext context, JsonObject json) {
         List<Ingredient> ingredientList = new LinkedList();
         JsonArray ingredients = JsonUtils.getJsonArray(json, "ingredients");
         Iterator var5 = ingredients.iterator();

         while(true) {
            JsonObject object;
            while(true) {
               if (!var5.hasNext()) {
                  return new ModItemList(ingredientList);
               }

               JsonElement ingredient = (JsonElement)var5.next();
               object = ingredient.getAsJsonObject();
               if (!JsonUtils.hasField(object, "modid")) {
                  break;
               }

               String mod = JsonUtils.getString(object, "modid");
               if (Loader.isModLoaded(mod)) {
                  break;
               }
            }

            if (JsonUtils.hasField(object, "ingredient")) {
               ingredientList.add(CraftingHelper.getIngredient(object.get("ingredient"), context));
            } else {
               ingredientList.add(CraftingHelper.getIngredient(object, context));
            }
         }
      }
   }
}
