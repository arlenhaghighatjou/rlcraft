package reborncore.api.praescriptum.ingredients.input;

import reborncore.api.praescriptum.ingredients.Ingredient;

public abstract class InputIngredient<T> extends Ingredient<T> {
   public final boolean consumable;

   protected InputIngredient(T ingredient) {
      this(ingredient, true);
   }

   protected InputIngredient(T ingredient, boolean consumable) {
      super(ingredient);
      this.consumable = consumable;
   }

   public abstract Object getUnspecific();

   public abstract InputIngredient<T> copy();

   public abstract int getCount();

   public abstract void shrink(int var1);

   public boolean isConsumable() {
      return this.consumable;
   }

   public boolean equals(Object object) {
      if (this.getClass() != object.getClass()) {
         return false;
      } else {
         return this.matches(((InputIngredient)object).ingredient) && this.consumable == ((InputIngredient)object).consumable;
      }
   }
}
