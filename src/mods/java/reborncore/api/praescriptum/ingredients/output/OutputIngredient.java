package reborncore.api.praescriptum.ingredients.output;

import reborncore.api.praescriptum.ingredients.Ingredient;

public abstract class OutputIngredient<T> extends Ingredient<T> {
   protected OutputIngredient(T ingredient) {
      super(ingredient);
   }

   public abstract OutputIngredient<T> copy();

   public int getCount() {
      return 0;
   }
}
