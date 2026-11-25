package mezz.jei.api.ingredients;

import java.util.Collection;
import mezz.jei.api.recipe.IIngredientType;

public interface IModIngredientRegistration {
   <V> void register(IIngredientType<V> var1, Collection<V> var2, IIngredientHelper<V> var3, IIngredientRenderer<V> var4);

   /** @deprecated */
   @Deprecated
   <V> void register(Class<V> var1, Collection<V> var2, IIngredientHelper<V> var3, IIngredientRenderer<V> var4);
}
