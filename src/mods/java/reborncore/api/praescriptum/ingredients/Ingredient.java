package reborncore.api.praescriptum.ingredients;

public abstract class Ingredient<T> {
   public final T ingredient;

   public Ingredient(T ingredient) {
      this.ingredient = ingredient;
   }

   public abstract boolean isEmpty();

   public abstract boolean matches(Object var1);

   public abstract boolean matchesStrict(Object var1);

   public abstract String toFormattedString();

   public boolean equals(Object object) {
      return this.getClass() != object.getClass() ? false : this.matches(((Ingredient)object).ingredient);
   }

   public String toString() {
      return String.format("Ingredient(%s)", this.toFormattedString());
   }
}
