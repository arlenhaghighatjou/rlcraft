package rustic.compat.jei;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IJeiHelpers;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.crafting.Recipes;

public class AdvancedAlchemyRecipeMaker {
   private AdvancedAlchemyRecipeMaker() {
   }

   public static List<ICondenserRecipe> getAlchemyRecipes(IJeiHelpers helpers) {
      List<ICondenserRecipe> recipes = new ArrayList();

      for(ICondenserRecipe recipe : Recipes.condenserRecipes) {
         if (recipe.isAdvanced()) {
            recipes.add(recipe);
         }
      }

      return recipes;
   }
}
