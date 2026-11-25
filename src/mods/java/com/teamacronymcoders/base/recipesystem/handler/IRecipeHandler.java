package com.teamacronymcoders.base.recipesystem.handler;

import com.teamacronymcoders.base.recipesystem.Recipe;
import com.teamacronymcoders.base.recipesystem.RecipeContainer;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;

public interface IRecipeHandler {
   boolean handleRecipe(RecipeContainer var1, @Nullable EntityPlayer var2);

   void reloadRecipes(List<Recipe> var1);
}
