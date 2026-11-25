package mezz.jei.recipes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.ImmutableList.Builder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.collect.ListMultiMap;
import mezz.jei.collect.Table;
import mezz.jei.ingredients.IngredientInformation;

public class RecipeMap {
   private final Table<IRecipeCategory, String, List<IRecipeWrapper>> recipeWrapperTable = Table.<IRecipeCategory, String, List<IRecipeWrapper>>hashBasedTable();
   private final ListMultiMap<String, String> categoryUidMap = new ListMultiMap<String, String>();
   private final Ordering<String> recipeCategoryOrdering;
   private final IIngredientRegistry ingredientRegistry;

   public RecipeMap(RecipeCategoryComparator recipeCategoryComparator, IIngredientRegistry ingredientRegistry) {
      this.recipeCategoryOrdering = Ordering.from(recipeCategoryComparator);
      this.ingredientRegistry = ingredientRegistry;
   }

   public <V> List<String> getRecipeCategories(V ingredient) {
      IIngredientHelper<V> ingredientHelper = this.ingredientRegistry.getIngredientHelper(ingredient);
      Set<String> recipeCategories = new HashSet();

      for(String key : IngredientInformation.getUniqueIdsWithWildcard(ingredientHelper, ingredient)) {
         recipeCategories.addAll(this.categoryUidMap.get(key));
      }

      return this.recipeCategoryOrdering.immutableSortedCopy(recipeCategories);
   }

   public <V> void addRecipeCategory(IRecipeCategory recipeCategory, V ingredient, IIngredientHelper<V> ingredientHelper) {
      String key = ingredientHelper.getUniqueId(ingredient);
      List<String> recipeCategories = (List)this.categoryUidMap.get(key);
      String recipeCategoryUid = recipeCategory.getUid();
      if (!recipeCategories.contains(recipeCategoryUid)) {
         recipeCategories.add(recipeCategoryUid);
      }

   }

   public <T extends IRecipeWrapper, V> ImmutableList<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory, V ingredient) {
      IIngredientHelper<V> ingredientHelper = this.ingredientRegistry.getIngredientHelper(ingredient);
      Map<String, List<IRecipeWrapper>> recipesForType = this.recipeWrapperTable.getRow(recipeCategory);
      Builder<T> listBuilder = ImmutableList.builder();

      for(String key : IngredientInformation.getUniqueIdsWithWildcard(ingredientHelper, ingredient)) {
         List<T> recipes = (List)recipesForType.get(key);
         if (recipes != null) {
            listBuilder.addAll(recipes);
         }
      }

      return listBuilder.build();
   }

   public <T extends IRecipeWrapper> void addRecipe(T recipeWrapper, IRecipeCategory<T> recipeCategory, Map<IIngredientType, List> ingredientsByType) {
      for(Entry<IIngredientType, List> entry : ingredientsByType.entrySet()) {
         if (entry != null) {
            this.addRecipe(recipeWrapper, recipeCategory, (IIngredientType)entry.getKey(), (List)entry.getValue());
         }
      }

   }

   private <T extends IRecipeWrapper, V> void addRecipe(T recipeWrapper, IRecipeCategory<T> recipeCategory, IIngredientType<V> ingredientType, List<V> ingredients) {
      IIngredientHelper<V> ingredientHelper = this.ingredientRegistry.getIngredientHelper(ingredientType);
      Map<String, List<IRecipeWrapper>> recipesWrappersForType = this.recipeWrapperTable.getRow(recipeCategory);
      Set<String> uniqueIds = new HashSet();

      for(V ingredient : ingredientHelper.expandSubtypes(ingredients)) {
         if (ingredient != null) {
            String key = ingredientHelper.getUniqueId(ingredient);
            if (!uniqueIds.contains(key)) {
               uniqueIds.add(key);
               List<T> recipeWrappers = (List)recipesWrappersForType.computeIfAbsent(key, (k) -> {
                  return (void)(new ArrayList());
               });
               recipeWrappers.add(recipeWrapper);
               this.addRecipeCategory(recipeCategory, ingredient, ingredientHelper);
            }
         }
      }

   }
}
