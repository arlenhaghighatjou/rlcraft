package com.teamacronymcoders.base.recipesystem.loader;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.recipesystem.Recipe;
import com.teamacronymcoders.base.recipesystem.source.IRecipeSource;
import com.teamacronymcoders.base.recipesystem.source.RecipeSource;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public class AssetJsonRecipeLoader extends JsonRecipeLoader {
   private static AssetJsonRecipeLoader instance;
   private final IRecipeSource RECIPE_SOURCE = new RecipeSource("assets", true);

   public AssetJsonRecipeLoader() {
   }

   public static AssetJsonRecipeLoader getInstance() {
      if (instance == null) {
         instance = new AssetJsonRecipeLoader();
      }

      return instance;
   }

   public IRecipeSource getRecipeSource() {
      return this.RECIPE_SOURCE;
   }

   public List<Recipe> loadRecipes() {
      return (List)Loader.instance().getActiveModList().parallelStream().map(this::loadRecipesForMod).flatMap(Collection::stream).collect(Collectors.toList());
   }

   private List<Recipe> loadRecipesForMod(ModContainer mod) {
      JsonContext ctx = new JsonContext(mod.getModId());
      List<Recipe> recipes = Lists.newArrayList();
      CraftingHelper.findFiles(mod, "assets/" + mod.getModId() + "/base/recipe_system", (root) -> {
         return true;
      }, (root, file) -> {
         Recipe recipe = this.loadRecipe(ctx, root, file);
         if (recipe != null) {
            recipes.add(recipe);
         }

         return true;
      }, true, true);
      return recipes;
   }
}
