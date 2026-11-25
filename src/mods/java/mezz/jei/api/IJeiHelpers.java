package mezz.jei.api;

import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.api.recipe.IVanillaRecipeFactory;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;

public interface IJeiHelpers {
   IGuiHelper getGuiHelper();

   IStackHelper getStackHelper();

   IIngredientBlacklist getIngredientBlacklist();

   IRecipeTransferHandlerHelper recipeTransferHandlerHelper();

   IVanillaRecipeFactory getVanillaRecipeFactory();

   /** @deprecated */
   @Deprecated
   IItemBlacklist getItemBlacklist();

   /** @deprecated */
   @Deprecated
   void reload();
}
