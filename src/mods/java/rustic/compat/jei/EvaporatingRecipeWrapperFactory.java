package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import rustic.common.crafting.IEvaporatingBasinRecipe;

public class EvaporatingRecipeWrapperFactory implements IRecipeWrapperFactory<IEvaporatingBasinRecipe> {
   public EvaporatingRecipeWrapperFactory() {
   }

   public IRecipeWrapper getRecipeWrapper(IEvaporatingBasinRecipe recipe) {
      return new EvaporatingRecipeWrapper(recipe);
   }
}
