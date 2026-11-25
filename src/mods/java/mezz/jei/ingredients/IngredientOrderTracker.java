package mezz.jei.ingredients;

import java.util.HashMap;
import java.util.Map;
import mezz.jei.api.ingredients.IIngredientHelper;

public class IngredientOrderTracker {
   private final Map<String, Integer> wildcardAddedOrder = new HashMap();
   private int addedIndex = 0;

   public IngredientOrderTracker() {
   }

   public <V> int getOrderIndex(V ingredient, IIngredientHelper<V> ingredientHelper) {
      String uid = ingredientHelper.getWildcardId(ingredient);
      if (this.wildcardAddedOrder.containsKey(uid)) {
         return ((Integer)this.wildcardAddedOrder.get(uid)).intValue();
      } else {
         int index = this.addedIndex;
         this.wildcardAddedOrder.put(uid, Integer.valueOf(index));
         ++this.addedIndex;
         return index;
      }
   }
}
