package com.teamacronymcoders.base.recipesystem.input;

import com.teamacronymcoders.base.recipesystem.RecipeContainer;

public interface IInput {
   boolean isMatched(RecipeContainer var1);

   void consume(RecipeContainer var1);
}
