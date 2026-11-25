package rustic.compat.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.ICrushingTubRecipe;

public class CrushingTubRecipeWrapper extends BlankRecipeWrapper {
   public ICrushingTubRecipe recipe = null;

   public CrushingTubRecipeWrapper(ICrushingTubRecipe recipe) {
      this.recipe = recipe;
   }

   public void getIngredients(IIngredients ingredients) {
      ingredients.setInput(ItemStack.class, this.recipe.getInput());
      ingredients.setOutput(FluidStack.class, this.recipe.getResult());
      ingredients.setOutput(ItemStack.class, this.recipe.getByproduct());
   }
}
