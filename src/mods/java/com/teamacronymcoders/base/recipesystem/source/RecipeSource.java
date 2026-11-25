package com.teamacronymcoders.base.recipesystem.source;

public class RecipeSource implements IRecipeSource {
   private final String name;
   private final boolean reload;

   public RecipeSource(String name, boolean reload) {
      this.name = name;
      this.reload = reload;
   }

   public String getName() {
      return this.name;
   }

   public boolean canReload() {
      return this.reload;
   }
}
