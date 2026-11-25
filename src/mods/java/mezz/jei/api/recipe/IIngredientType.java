package mezz.jei.api.recipe;

public interface IIngredientType<T> {
   Class<? extends T> getIngredientClass();
}
