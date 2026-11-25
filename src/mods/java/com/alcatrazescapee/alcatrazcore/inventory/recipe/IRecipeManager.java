package com.alcatrazescapee.alcatrazcore.inventory.recipe;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IRecipeManager<T extends IRecipeCore> {
   void add(T var1);

   @Nonnull
   List<T> getAll();

   /** @deprecated */
   @Nullable
   @Deprecated
   T get(Object... var1);

   @Nullable
   T get(Object var1);

   /** @deprecated */
   @Deprecated
   void remove(Object... var1);

   void remove(Object var1);

   /** @deprecated */
   @Deprecated
   default boolean isRecipe(Object... inputs) {
      return false;
   }

   default boolean isRecipe(Object input) {
      return this.get(input) != null;
   }
}
