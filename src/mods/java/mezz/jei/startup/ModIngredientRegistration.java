package mezz.jei.startup;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.ingredients.IngredientBlacklistInternal;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.util.ErrorUtil;
import mezz.jei.util.IngredientSet;

public class ModIngredientRegistration implements IModIngredientRegistration {
   private final Map<IIngredientType, Collection> allIngredientsMap = new IdentityHashMap();
   private final Map<IIngredientType, IIngredientHelper> ingredientHelperMap = new IdentityHashMap();
   private final Map<IIngredientType, IIngredientRenderer> ingredientRendererMap = new IdentityHashMap();

   public ModIngredientRegistration() {
   }

   public <V> void register(IIngredientType<V> ingredientType, Collection<V> allIngredients, IIngredientHelper<V> ingredientHelper, IIngredientRenderer<V> ingredientRenderer) {
      ErrorUtil.checkNotNull(ingredientType, "ingredientType");
      ErrorUtil.checkNotNull(allIngredients, "allIngredients");
      ErrorUtil.checkNotNull(ingredientHelper, "ingredientHelper");
      ErrorUtil.checkNotNull(ingredientRenderer, "ingredientRenderer");
      this.allIngredientsMap.put(ingredientType, allIngredients);
      this.ingredientHelperMap.put(ingredientType, ingredientHelper);
      this.ingredientRendererMap.put(ingredientType, ingredientRenderer);
   }

   /** @deprecated */
   @Deprecated
   public <V> void register(Class<V> ingredientClass, Collection<V> allIngredients, IIngredientHelper<V> ingredientHelper, IIngredientRenderer<V> ingredientRenderer) {
      ErrorUtil.checkNotNull(ingredientClass, "ingredientClass");
      this.register(() -> {
         return (void)ingredientClass;
      }, allIngredients, ingredientHelper, ingredientRenderer);
   }

   public IngredientRegistry createIngredientRegistry(IModIdHelper modIdHelper, IngredientBlacklistInternal blacklist) {
      Map<IIngredientType, IngredientSet> ingredientsMap = new IdentityHashMap();

      for(Entry<IIngredientType, Collection> entry : this.allIngredientsMap.entrySet()) {
         IIngredientType ingredientType = (IIngredientType)entry.getKey();
         IngredientSet ingredientSet = this.createIngredientSet(ingredientType, (Collection)entry.getValue());
         ingredientsMap.put(ingredientType, ingredientSet);
      }

      return new IngredientRegistry(modIdHelper, blacklist, ingredientsMap, ImmutableMap.copyOf(this.ingredientHelperMap), ImmutableMap.copyOf(this.ingredientRendererMap));
   }

   private <T> IngredientSet<T> createIngredientSet(IIngredientType<T> ingredientType, Collection<T> ingredients) {
      IIngredientHelper<T> ingredientHelper = (IIngredientHelper)this.ingredientHelperMap.get(ingredientType);
      IngredientSet<T> ingredientSet = IngredientSet.<T>create(ingredientType, ingredientHelper);
      ingredientSet.addAll(ingredients);
      return ingredientSet;
   }
}
