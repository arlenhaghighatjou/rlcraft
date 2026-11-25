package org.jwaresoftware.mcmods.lib.api.mod;

import javax.annotation.Nonnull;

public interface IModConfig {
   boolean isPresent(@Nonnull String var1);

   boolean isAllowed(@Nonnull String var1, boolean var2);

   boolean isDebugMode();

   boolean isSuperCompatibilityMode();

   void persist();

   void changed();
}
