package com.teamacronymcoders.base.recipesystem.type;

import com.google.common.collect.Lists;
import com.teamacronymcoders.base.recipesystem.RecipeSystem;
import com.teamacronymcoders.base.recipesystem.handler.DefaultRecipeHandler;
import com.teamacronymcoders.base.recipesystem.handler.IRecipeHandler;
import java.util.List;
import net.minecraft.util.ResourceLocation;

public class RecipeType {
   public final String name;
   public final List<IRecipeHandler> recipeHandlers;

   public RecipeType(ResourceLocation name) {
      this.name = name.toString();
      this.recipeHandlers = Lists.newArrayList();
   }

   public IRecipeHandler createHandler() {
      return new DefaultRecipeHandler(RecipeSystem.getRecipesFor(this));
   }

   public List<IRecipeHandler> getRecipeHandlers() {
      return this.recipeHandlers;
   }
}
