package com.blamejared.mtlib.utils;

import com.blamejared.mtlib.helpers.LogHelper;
import java.util.List;

public abstract class BaseListAddition<T> extends BaseListModification<T> {
   protected BaseListAddition(String name, List<T> list) {
      super(name, list);
   }

   protected BaseListAddition(String name, List<T> list, List<T> recipies) {
      this(name, list);
      if (this.recipes != null) {
         this.recipes.addAll(recipies);
      }

   }

   public void apply() {
      if (!this.recipes.isEmpty()) {
         for(T recipe : this.recipes) {
            if (recipe != null) {
               if (this.list.add(recipe)) {
                  this.successful.add(recipe);
               } else {
                  LogHelper.logError(String.format("Error adding %s Recipe for %s", this.name, this.getRecipeInfo(recipe)));
               }
            } else {
               LogHelper.logError(String.format("Error adding %s Recipe: null object", this.name));
            }
         }

      }
   }

   public String describe() {
      return String.format("Adding %d %s Recipe(s) for %s", this.recipes.size(), this.name, this.getRecipeInfo());
   }
}
