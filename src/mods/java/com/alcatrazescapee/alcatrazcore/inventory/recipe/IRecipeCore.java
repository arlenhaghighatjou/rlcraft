package com.alcatrazescapee.alcatrazcore.inventory.recipe;

import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface IRecipeCore extends Predicate<Object> {
   boolean test(Object var1);

   /** @deprecated */
   @Deprecated
   boolean test(Object... var1);

   boolean matches(Object var1);

   /** @deprecated */
   @Deprecated
   boolean matches(Object... var1);

   Object getOutput();

   Object getInput();

   String getName();
}
