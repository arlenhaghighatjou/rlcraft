package mezz.jei.api.recipe;

/** @deprecated */
@Deprecated
public interface IRecipeHandler<T> {
   Class<T> getRecipeClass();

   String getRecipeCategoryUid(T var1);

   IRecipeWrapper getRecipeWrapper(T var1);

   boolean isRecipeValid(T var1);
}
