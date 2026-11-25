package xzeroair.trinkets.util.recipes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;

public class RecipeHelperUtil {
   public RecipeHelperUtil() {
   }

   public static ShapedPrimer parseShaped(JsonContext context, JsonObject json) {
      Map<Character, Ingredient> ingredientMap = Maps.newHashMap();

      for(Entry<String, JsonElement> entry : JsonUtils.getJsonObject(json, "key").entrySet()) {
         if (((String)entry.getKey()).length() != 1) {
            throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
         }

         if (" ".equals(entry.getKey())) {
            throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
         }

         ingredientMap.put(Character.valueOf(((String)entry.getKey()).toCharArray()[0]), CraftingHelper.getIngredient((JsonElement)entry.getValue(), context));
      }

      ingredientMap.put(Character.valueOf(' '), Ingredient.EMPTY);
      JsonArray patternJ = JsonUtils.getJsonArray(json, "pattern");
      if (patternJ.size() == 0) {
         throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
      } else {
         String[] pattern = new String[patternJ.size()];

         for(int x = 0; x < pattern.length; ++x) {
            String line = JsonUtils.getString(patternJ.get(x), "pattern[" + x + "]");
            if (x > 0 && pattern[0].length() != line.length()) {
               throw new JsonSyntaxException("Invalid pattern: each row must  be the same width");
            }

            pattern[x] = line;
         }

         ShapedPrimer primer = new ShapedPrimer();
         primer.width = pattern[0].length();
         primer.height = pattern.length;
         primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
         primer.input = NonNullList.withSize(primer.width * primer.height, Ingredient.EMPTY);
         Set<Character> keys = Sets.newHashSet(ingredientMap.keySet());
         keys.remove(Character.valueOf(' '));
         int index = 0;

         for(String line : pattern) {
            for(char chr : line.toCharArray()) {
               Ingredient ing = (Ingredient)ingredientMap.get(Character.valueOf(chr));
               if (ing == null) {
                  throw new JsonSyntaxException("Pattern references symbol '" + chr + "' but it's not defined in the key");
               }

               primer.input.set(index++, ing);
               keys.remove(Character.valueOf(chr));
            }
         }

         if (!keys.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + keys);
         } else {
            return primer;
         }
      }
   }

   public static NonNullList<Ingredient> parseShapeless(JsonContext context, JsonObject json) {
      NonNullList<Ingredient> ingredients = NonNullList.create();

      for(JsonElement element : JsonUtils.getJsonArray(json, "ingredients")) {
         ingredients.add(CraftingHelper.getIngredient(element, context));
      }

      if (ingredients.isEmpty()) {
         throw new JsonParseException("No ingredients for shapeless recipe");
      } else {
         return ingredients;
      }
   }
}
