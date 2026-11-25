package mezz.jei.util;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.recipe.IRecipeCategory;

public final class LegacyUtil {
   private LegacyUtil() {
   }

   public static <T> T getIngredientCopy(T value, IIngredientHelper<T> ingredientHelper) {
      try {
         return ingredientHelper.copyIngredient(value);
      } catch (AbstractMethodError var3) {
         return value;
      }
   }

   public static List<String> getTooltipStrings(IRecipeCategory<?> recipeCategory, int recipeMouseX, int recipeMouseY) {
      try {
         return recipeCategory.getTooltipStrings(recipeMouseX, recipeMouseY);
      } catch (AbstractMethodError var4) {
         return Collections.emptyList();
      }
   }

   public static <T> String getResourceId(T ingredient, IIngredientHelper<T> ingredientHelper) {
      try {
         return ingredientHelper.getResourceId(ingredient);
      } catch (AbstractMethodError var3) {
         return ingredientHelper.getUniqueId(ingredient);
      }
   }

   @Nullable
   public static String getModName(IRecipeCategory recipeCategory) {
      try {
         return recipeCategory.getModName();
      } catch (AbstractMethodError var2) {
         return null;
      }
   }
}
