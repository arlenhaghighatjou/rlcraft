package mezz.jei.api.recipe;

import java.util.List;

public interface IRecipeRegistryPlugin {
   <V> List<String> getRecipeCategoryUids(IFocus<V> var1);

   <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(IRecipeCategory<T> var1, IFocus<V> var2);

   <T extends IRecipeWrapper> List<T> getRecipeWrappers(IRecipeCategory<T> var1);
}
