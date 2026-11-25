package mezz.jei.ingredients;

import java.util.HashSet;
import java.util.Set;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.config.Config;

public class IngredientBlacklistInternal {
   private final Set<String> ingredientBlacklist = new HashSet();

   public IngredientBlacklistInternal() {
   }

   public <V> void addIngredientToBlacklist(V ingredient, IIngredientHelper<V> ingredientHelper) {
      String uniqueName = ingredientHelper.getUniqueId(ingredient);
      this.ingredientBlacklist.add(uniqueName);
   }

   public <V> void removeIngredientFromBlacklist(V ingredient, IIngredientHelper<V> ingredientHelper) {
      String uniqueName = ingredientHelper.getUniqueId(ingredient);
      this.ingredientBlacklist.remove(uniqueName);
   }

   public <V> boolean isIngredientBlacklisted(V ingredient, IIngredientHelper<V> ingredientHelper) {
      return this.isIngredientBlacklistedByApi(ingredient, ingredientHelper) || Config.isIngredientOnConfigBlacklist(ingredient, ingredientHelper);
   }

   public <V> boolean isIngredientBlacklistedByApi(V ingredient, IIngredientHelper<V> ingredientHelper) {
      for(String uid : IngredientInformation.getUniqueIdsWithWildcard(ingredientHelper, ingredient)) {
         if (this.ingredientBlacklist.contains(uid)) {
            return true;
         }
      }

      return false;
   }
}
