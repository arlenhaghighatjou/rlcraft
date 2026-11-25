package mezz.jei.plugins.vanilla.crafting;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ShapedOreRecipeWrapper extends ShapelessRecipeWrapper<ShapedOreRecipe> implements IShapedCraftingRecipeWrapper {
   public ShapedOreRecipeWrapper(IJeiHelpers jeiHelpers, ShapedOreRecipe recipe) {
      super(jeiHelpers, recipe);
   }

   public int getWidth() {
      return ((ShapedOreRecipe)this.recipe).getWidth();
   }

   public int getHeight() {
      return ((ShapedOreRecipe)this.recipe).getHeight();
   }
}
