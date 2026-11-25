package com.blamejared.mtlib.utils;

import forestry.api.recipes.ICraftingProvider;
import forestry.api.recipes.IForestryRecipe;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseRemoveForestry<R extends IForestryRecipe> extends BaseAction {
   protected ICraftingProvider<R> provider;

   protected BaseRemoveForestry(String name, ICraftingProvider<R> provider) {
      super(name);
      this.provider = provider;
   }

   public void apply() {
      List<R> toRemove = new ArrayList();

      for(R recipe : this.provider.recipes()) {
         if (this.checkIsRecipe(recipe)) {
            toRemove.add(recipe);
         }
      }

      for(R recipe : toRemove) {
         this.provider.removeRecipe(recipe);
      }

   }

   public abstract boolean checkIsRecipe(R var1);
}
