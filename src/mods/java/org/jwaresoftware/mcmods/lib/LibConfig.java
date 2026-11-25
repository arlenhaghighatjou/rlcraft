package org.jwaresoftware.mcmods.lib;

public final class LibConfig {
   private static LibConfig _sharedINSTANCE = new LibConfig();

   public LibConfig() {
   }

   public LibConfig setInstance() {
      _sharedINSTANCE = this;
      return this;
   }

   public static final LibConfig getInstance() {
      return _sharedINSTANCE;
   }
}
