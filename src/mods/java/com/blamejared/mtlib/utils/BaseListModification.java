package com.blamejared.mtlib.utils;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseListModification<T> extends BaseAction {
   protected final List<T> list;
   protected final LinkedList<T> recipes;
   protected final LinkedList<T> successful;

   protected BaseListModification(String name, List<T> list) {
      super(name);
      this.list = list;
      this.recipes = new LinkedList();
      this.successful = new LinkedList();
   }

   protected String getRecipeInfo() {
      if (!this.recipes.isEmpty()) {
         StringBuilder sb = new StringBuilder();

         for(T recipe : this.recipes) {
            if (recipe != null) {
               sb.append(this.getRecipeInfo(recipe)).append(", ");
            }
         }

         if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
         }

         return sb.toString();
      } else {
         return super.getRecipeInfo();
      }
   }

   protected abstract String getRecipeInfo(T var1);

   public int hashCode() {
      return 37 * super.hashCode() ^ (this.list != null ? 43 * this.list.hashCode() : 0);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (!super.equals(obj)) {
         return false;
      } else if (!(obj instanceof BaseListModification)) {
         return false;
      } else {
         BaseListModification<?> mod = (BaseListModification)obj;
         if (this.list != mod.list) {
            return false;
         } else if (this.recipes.size() != mod.recipes.size()) {
            return false;
         } else {
            for(T recipe : this.recipes) {
               boolean found = false;

               for(T otherRecipe : mod.recipes) {
                  if (this.equals(recipe, otherRecipe)) {
                     found = true;
                     break;
                  }
               }

               if (!found) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   protected boolean equals(T recipe, T otherRecipe) {
      if (recipe == otherRecipe) {
         return true;
      } else {
         return recipe.equals(otherRecipe);
      }
   }
}
