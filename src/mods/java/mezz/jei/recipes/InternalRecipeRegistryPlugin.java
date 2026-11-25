package mezz.jei.recipes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableList.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeRegistryPlugin;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.collect.ListMultiMap;
import mezz.jei.gui.Focus;
import mezz.jei.ingredients.IngredientInformation;

public class InternalRecipeRegistryPlugin implements IRecipeRegistryPlugin {
   private final RecipeRegistry recipeRegistry;
   private final ImmutableMultimap<String, String> categoriesForRecipeCatalystKeys;
   private final IIngredientRegistry ingredientRegistry;
   private final ImmutableMap<String, IRecipeCategory> recipeCategoriesMap;
   private final RecipeMap recipeInputMap;
   private final RecipeMap recipeOutputMap;
   private final ListMultiMap<IRecipeCategory, IRecipeWrapper> recipeWrappersForCategories;

   public InternalRecipeRegistryPlugin(RecipeRegistry recipeRegistry, ImmutableMultimap<String, String> categoriesForRecipeCatalystKeys, IIngredientRegistry ingredientRegistry, ImmutableMap<String, IRecipeCategory> recipeCategoriesMap, RecipeMap recipeInputMap, RecipeMap recipeOutputMap, ListMultiMap<IRecipeCategory, IRecipeWrapper> recipeWrappersForCategories) {
      this.recipeRegistry = recipeRegistry;
      this.categoriesForRecipeCatalystKeys = categoriesForRecipeCatalystKeys;
      this.ingredientRegistry = ingredientRegistry;
      this.recipeCategoriesMap = recipeCategoriesMap;
      this.recipeInputMap = recipeInputMap;
      this.recipeOutputMap = recipeOutputMap;
      this.recipeWrappersForCategories = recipeWrappersForCategories;
   }

   public <V> List<String> getRecipeCategoryUids(IFocus<V> focus) {
      IFocus<V> var3 = Focus.check(focus);
      V ingredient = (V)var3.getValue();
      switch(var3.getMode()) {
      case INPUT:
         return this.recipeInputMap.getRecipeCategories(ingredient);
      case OUTPUT:
         return this.recipeOutputMap.getRecipeCategories(ingredient);
      default:
         return this.getRecipeCategories();
      }
   }

   private ImmutableList<String> getRecipeCategories() {
      Builder<String> builder = ImmutableList.builder();
      UnmodifiableIterator var2 = this.recipeCategoriesMap.entrySet().iterator();

      while(var2.hasNext()) {
         Entry<String, IRecipeCategory> entry = (Entry)var2.next();
         IRecipeCategory<?> recipeCategory = (IRecipeCategory)entry.getValue();
         if (!this.recipeRegistry.getRecipeWrappers(recipeCategory).isEmpty()) {
            builder.add(entry.getKey());
         }
      }

      return builder.build();
   }

   public <T extends IRecipeWrapper, V> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {
      IFocus<V> var14 = Focus.check(focus);
      V ingredient = (V)var14.getValue();
      IIngredientHelper<V> ingredientHelper = this.ingredientRegistry.getIngredientHelper(ingredient);
      if (var14.getMode() == IFocus.Mode.INPUT) {
         ImmutableList<T> recipes = this.recipeInputMap.getRecipeWrappers(recipeCategory, ingredient);
         String recipeCategoryUid = recipeCategory.getUid();

         for(String inputKey : IngredientInformation.getUniqueIdsWithWildcard(ingredientHelper, ingredient)) {
            if (this.categoriesForRecipeCatalystKeys.get(inputKey).contains(recipeCategoryUid)) {
               ImmutableSet<T> specificRecipes = ImmutableSet.copyOf(recipes);
               List<T> recipesForCategory = (List)this.recipeWrappersForCategories.get(recipeCategory);
               List<T> allRecipes = new ArrayList(recipes);

               for(T recipe : recipesForCategory) {
                  if (!specificRecipes.contains(recipe)) {
                     allRecipes.add(recipe);
                  }
               }

               return allRecipes;
            }
         }

         return recipes;
      } else {
         return this.recipeOutputMap.getRecipeWrappers(recipeCategory, ingredient);
      }
   }

   public <T extends IRecipeWrapper> List<T> getRecipeWrappers(IRecipeCategory<T> recipeCategory) {
      List<T> recipeWrappers = (List)this.recipeWrappersForCategories.get(recipeCategory);
      return Collections.unmodifiableList(recipeWrappers);
   }
}
