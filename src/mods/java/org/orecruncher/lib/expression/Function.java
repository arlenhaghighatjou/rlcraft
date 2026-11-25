package org.orecruncher.lib.expression;

public abstract class Function extends LazyFunction {
   public Function(String name, int numParams) {
      super(name, numParams);
   }

   public LazyVariant lazyEval(LazyVariant... lazyParams) {
      IVariant[] params = new IVariant[lazyParams.length];

      for(int i = 0; i < lazyParams.length; ++i) {
         params[i] = lazyParams[i].eval();
      }

      return () -> {
         return this.eval(params);
      };
   }

   public abstract IVariant eval(IVariant... var1);
}
