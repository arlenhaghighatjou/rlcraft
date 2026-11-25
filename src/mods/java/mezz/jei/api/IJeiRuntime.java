package mezz.jei.api;

public interface IJeiRuntime {
   IRecipeRegistry getRecipeRegistry();

   IRecipesGui getRecipesGui();

   IIngredientFilter getIngredientFilter();

   IIngredientListOverlay getIngredientListOverlay();

   IBookmarkOverlay getBookmarkOverlay();

   /** @deprecated */
   @Deprecated
   IItemListOverlay getItemListOverlay();
}
