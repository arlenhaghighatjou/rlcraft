package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import rustic.common.crafting.ICondenserRecipe;

public class AdvancedAlchemyRecipeWrapperFactory implements IRecipeWrapperFactory<ICondenserRecipe> {
   public AdvancedAlchemyRecipeWrapperFactory() {
   }

   public IRecipeWrapper getRecipeWrapper(ICondenserRecipe recipe) {
      return new AdvancedAlchemyRecipeWrapper(recipe);
   }
}
