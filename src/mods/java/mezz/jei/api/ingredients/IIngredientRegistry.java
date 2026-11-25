package mezz.jei.api.ingredients;

import java.util.Collection;
import java.util.List;
import mezz.jei.api.recipe.IIngredientType;
import net.minecraft.item.ItemStack;

public interface IIngredientRegistry {
   <V> Collection<V> getAllIngredients(IIngredientType<V> var1);

   <V> IIngredientHelper<V> getIngredientHelper(V var1);

   <V> IIngredientHelper<V> getIngredientHelper(IIngredientType<V> var1);

   <V> IIngredientRenderer<V> getIngredientRenderer(V var1);

   <V> IIngredientRenderer<V> getIngredientRenderer(IIngredientType<V> var1);

   Collection<IIngredientType> getRegisteredIngredientTypes();

   List<ItemStack> getFuels();

   List<ItemStack> getPotionIngredients();

   <V> void addIngredientsAtRuntime(IIngredientType<V> var1, Collection<V> var2);

   <V> void removeIngredientsAtRuntime(IIngredientType<V> var1, Collection<V> var2);

   <V> IIngredientType<V> getIngredientType(V var1);

   <V> IIngredientType<V> getIngredientType(Class<? extends V> var1);

   /** @deprecated */
   @Deprecated
   <V> Collection<V> getAllIngredients(Class<V> var1);

   /** @deprecated */
   @Deprecated
   <V> IIngredientHelper<V> getIngredientHelper(Class<? extends V> var1);

   /** @deprecated */
   @Deprecated
   <V> IIngredientRenderer<V> getIngredientRenderer(Class<? extends V> var1);

   /** @deprecated */
   @Deprecated
   Collection<Class> getRegisteredIngredientClasses();

   /** @deprecated */
   @Deprecated
   <V> void addIngredientsAtRuntime(Class<V> var1, Collection<V> var2);

   /** @deprecated */
   @Deprecated
   <V> void removeIngredientsAtRuntime(Class<V> var1, Collection<V> var2);

   /** @deprecated */
   @Deprecated
   <V> void addIngredientsAtRuntime(Class<V> var1, List<V> var2);

   /** @deprecated */
   @Deprecated
   <V> void removeIngredientsAtRuntime(Class<V> var1, List<V> var2);

   /** @deprecated */
   @Deprecated
   <V> List<V> getIngredients(Class<V> var1);
}
