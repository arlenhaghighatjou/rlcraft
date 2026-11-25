package mezz.jei.plugins.vanilla.crafting;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.item.crafting.ShapedRecipes;

public class ShapedRecipesWrapper extends ShapelessRecipeWrapper<ShapedRecipes> implements IShapedCraftingRecipeWrapper {
   public ShapedRecipesWrapper(IJeiHelpers jeiHelpers, ShapedRecipes recipe) {
      super(jeiHelpers, recipe);
   }

   public int getWidth() {
      return ((ShapedRecipes)this.recipe).recipeWidth;
   }

   public int getHeight() {
      return ((ShapedRecipes)this.recipe).recipeHeight;
   }
}
