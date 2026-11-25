package org.orecruncher.lib.expression;

import java.util.Locale;

public abstract class LazyFunction {
   private final String name;
   private final int numParams;

   public LazyFunction(String name, int numParams) {
      this.name = name.toUpperCase(Locale.ROOT);
      this.numParams = numParams;
   }

   public String getName() {
      return this.name;
   }

   public int getNumParams() {
      return this.numParams;
   }

   public boolean numParamsVaries() {
      return this.numParams < 0;
   }

   public abstract LazyVariant lazyEval(LazyVariant... var1);
}
