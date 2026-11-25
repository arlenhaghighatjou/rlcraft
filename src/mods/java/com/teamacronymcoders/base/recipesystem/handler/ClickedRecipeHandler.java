package com.teamacronymcoders.base.recipesystem.handler;

import com.teamacronymcoders.base.recipesystem.Recipe;
import com.teamacronymcoders.base.recipesystem.RecipeContainer;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;

public class ClickedRecipeHandler implements IRecipeHandler {
   private final List<Recipe> recipes;
   private Recipe currentRecipe = null;

   public ClickedRecipeHandler(List<Recipe> recipes) {
      this.recipes = recipes;
   }

   public boolean handleRecipe(RecipeContainer param1, @Nullable EntityPlayer param2) {
      // $FF: Couldn't be decompiled
   }

   public void reloadRecipes(List<Recipe> recipeList) {
      this.recipes.clear();
      this.recipes.addAll(recipeList);
      this.currentRecipe = null;
   }
}
