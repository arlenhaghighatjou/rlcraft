package mezz.jei.gui.recipes;

import com.google.common.collect.ImmutableList;
import java.util.List;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;

public interface IRecipeGuiLogic {
   String getPageString();

   void setRecipesPerPage(int var1);

   boolean hasMultipleCategories();

   boolean hasAllCategories();

   void previousRecipeCategory();

   void nextRecipeCategory();

   void setRecipeCategory(IRecipeCategory var1);

   boolean hasMultiplePages();

   void previousPage();

   void nextPage();

   <V> boolean setFocus(IFocus<V> var1);

   boolean back();

   void clearHistory();

   boolean setCategoryFocus();

   boolean setCategoryFocus(List<String> var1);

   IRecipeCategory getSelectedRecipeCategory();

   ImmutableList<IRecipeCategory> getRecipeCategories();

   List<Object> getRecipeCatalysts();

   List<Object> getRecipeCatalysts(IRecipeCategory var1);

   List<RecipeLayout> getRecipeLayouts(int var1, int var2, int var3);
}
