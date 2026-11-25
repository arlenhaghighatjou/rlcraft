package com.alcatrazescapee.alcatrazcore.inventory.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RecipeManager<T extends IRecipeCore> implements IRecipeManager<T> {
   private final List<T> recipes;

   public RecipeManager() {
      this.recipes = new ArrayList();
   }

   public RecipeManager(int size) {
      this.recipes = new ArrayList(size);
   }

   public void add(T recipe) {
      this.recipes.add(recipe);
   }

   /** @deprecated */
   @Nullable
   @Deprecated
   public T get(Object... inputs) {
      return (T)(this.recipes.stream().filter((x) -> {
         return (T)x.test(inputs);
      }).findFirst().orElse((Object)null));
   }

   @Nullable
   public T get(Object input) {
      return (T)(this.recipes.stream().filter((x) -> {
         return (T)x.test(input);
      }).findFirst().orElse((Object)null));
   }

   @Nonnull
   public List<T> getAll() {
      return Collections.unmodifiableList(this.recipes);
   }

   /** @deprecated */
   @Deprecated
   public void remove(Object... inputs) {
      this.recipes.removeIf((x) -> {
         return x.matches(inputs);
      });
   }

   public void remove(Object input) {
      this.recipes.removeIf((x) -> {
         return x.matches(input);
      });
   }
}
