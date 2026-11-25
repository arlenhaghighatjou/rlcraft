package mezz.jei.api.recipe;

@FunctionalInterface
public interface IRecipeWrapperFactory<T> {
   IRecipeWrapper getRecipeWrapper(T var1);
}
