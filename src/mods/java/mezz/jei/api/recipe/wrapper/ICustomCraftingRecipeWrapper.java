package mezz.jei.api.recipe.wrapper;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;

public interface ICustomCraftingRecipeWrapper extends ICraftingRecipeWrapper {
   void setRecipe(IRecipeLayout var1, IIngredients var2);
}
