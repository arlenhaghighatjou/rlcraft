package mezz.jei.api;

import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

public interface IModPlugin {
   default void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
   }

   default void registerIngredients(IModIngredientRegistration registry) {
   }

   default void registerCategories(IRecipeCategoryRegistration registry) {
   }

   default void register(IModRegistry registry) {
   }

   default void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
   }
}
