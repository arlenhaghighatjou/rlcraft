package com.teamacronymcoders.base.recipesystem.output;

import com.teamacronymcoders.base.recipesystem.RecipeContainer;
import java.util.List;
import java.util.function.Predicate;

public class OneOfOutput implements IOutput {
   private final List<IOutput> outputList;

   public OneOfOutput(List<IOutput> outputList) {
      this.outputList = outputList;
   }

   public boolean canOutput(RecipeContainer recipeContainer) {
      return this.outputList.parallelStream().anyMatch((output) -> {
         return output.canOutput(recipeContainer);
      });
   }

   public void output(RecipeContainer recipeContainer) {
      boolean hasOutput = false;
      int tries = 0;

      while(true) {
         IOutput output = (IOutput)this.outputList.get(recipeContainer.getWorld().rand.nextInt(this.outputList.size()));
         if (output.canOutput(recipeContainer)) {
            output.output(recipeContainer);
            hasOutput = true;
         }

         ++tries;
         if (hasOutput || tries >= 3) {
            break;
         }
      }

   }
}
