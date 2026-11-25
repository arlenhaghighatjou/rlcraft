package com.teamacronymcoders.base.recipesystem.output;

import com.teamacronymcoders.base.recipesystem.RecipeContainer;

public interface IOutput {
   boolean canOutput(RecipeContainer var1);

   void output(RecipeContainer var1);
}
