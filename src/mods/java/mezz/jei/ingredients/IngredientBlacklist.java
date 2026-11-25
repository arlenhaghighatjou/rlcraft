package mezz.jei.ingredients;

import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.util.ErrorUtil;

public class IngredientBlacklist implements IIngredientBlacklist {
   private final IIngredientRegistry ingredientRegistry;
   private final IngredientBlacklistInternal internal;

   public IngredientBlacklist(IIngredientRegistry ingredientRegistry, IngredientBlacklistInternal internal) {
      this.ingredientRegistry = ingredientRegistry;
      this.internal = internal;
   }

   public <V> void addIngredientToBlacklist(V ingredient) {
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      IIngredientHelper<V> ingredientHelper = this.ingredientRegistry.getIngredientHelper(ingredient);
      this.internal.addIngredientToBlacklist(ingredient, ingredientHelper);
   }

   public <V> void removeIngredientFromBlacklist(V ingredient) {
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      IIngredientHelper<V> ingredientHelper = this.ingredientRegistry.getIngredientHelper(ingredient);
      this.internal.removeIngredientFromBlacklist(ingredient, ingredientHelper);
   }

   public <V> boolean isIngredientBlacklisted(V ingredient) {
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      IIngredientHelper<V> ingredientHelper = this.ingredientRegistry.getIngredientHelper(ingredient);
      return this.internal.isIngredientBlacklisted(ingredient, ingredientHelper);
   }
}
