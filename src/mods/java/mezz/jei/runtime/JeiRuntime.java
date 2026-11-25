package mezz.jei.runtime;

import mezz.jei.api.IBookmarkOverlay;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.gui.overlay.IngredientListOverlay;
import mezz.jei.gui.overlay.ItemListOverlay;
import mezz.jei.gui.recipes.RecipesGui;
import mezz.jei.ingredients.IngredientFilter;
import mezz.jei.recipes.RecipeRegistry;

public class JeiRuntime implements IJeiRuntime {
   private final RecipeRegistry recipeRegistry;
   private final IngredientListOverlay ingredientListOverlay;
   private final IBookmarkOverlay bookmarkOverlay;
   private final ItemListOverlay itemListOverlay;
   private final RecipesGui recipesGui;
   private final IngredientFilter ingredientFilter;

   public JeiRuntime(RecipeRegistry recipeRegistry, IngredientListOverlay ingredientListOverlay, IBookmarkOverlay bookmarkOverlay, RecipesGui recipesGui, IngredientFilter ingredientFilter) {
      this.recipeRegistry = recipeRegistry;
      this.ingredientListOverlay = ingredientListOverlay;
      this.bookmarkOverlay = bookmarkOverlay;
      this.recipesGui = recipesGui;
      this.ingredientFilter = ingredientFilter;
      this.itemListOverlay = new ItemListOverlay(ingredientListOverlay, ingredientFilter);
   }

   public void close() {
      this.recipesGui.close();
   }

   public RecipeRegistry getRecipeRegistry() {
      return this.recipeRegistry;
   }

   public ItemListOverlay getItemListOverlay() {
      return this.itemListOverlay;
   }

   public IngredientFilter getIngredientFilter() {
      return this.ingredientFilter;
   }

   public IngredientListOverlay getIngredientListOverlay() {
      return this.ingredientListOverlay;
   }

   public IBookmarkOverlay getBookmarkOverlay() {
      return this.bookmarkOverlay;
   }

   public RecipesGui getRecipesGui() {
      return this.recipesGui;
   }
}
