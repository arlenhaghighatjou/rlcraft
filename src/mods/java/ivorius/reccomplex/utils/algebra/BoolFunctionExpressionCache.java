package ivorius.reccomplex.utils.algebra;

import ivorius.reccomplex.RCConfig;
import java.util.function.Function;
import java.util.function.Predicate;

public class BoolFunctionExpressionCache<A, U> extends FunctionExpressionCache<Boolean, A, U> implements Predicate<A> {
   public static final String GLOBAL_PREFIX = "global:";

   public BoolFunctionExpressionCache(Algebra<Boolean> algebra) {
      super(algebra);
      this.addBoolConstants();
   }

   public BoolFunctionExpressionCache(Algebra<Boolean> algebra, Boolean emptyResult, String emptyResultRepresentation) {
      super(algebra, emptyResult, emptyResultRepresentation);
      this.addBoolConstants();
   }

   protected void addBoolConstants() {
      this.addType(FunctionExpressionCaches.constant("true", Boolean.valueOf(true)));
      this.addType(FunctionExpressionCaches.constant("false", Boolean.valueOf(false)));
      this.addType(new BoolFunctionExpressionCache.VariableTypeGlobal("global:", ""));
   }

   public boolean test(A a) {
      return ((Boolean)this.evaluate(a)).booleanValue();
   }

   public static class VariableTypeGlobal extends FunctionExpressionCache.VariableType<Boolean, Object, Object> {
      public VariableTypeGlobal(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<Object>, Boolean> parse(String var) {
         return (o) -> {
            return (Boolean)RCConfig.globalToggles.getOrDefault(var, Boolean.valueOf(false));
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object o) {
         return RCConfig.globalToggles.containsKey(var) ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN;
      }
   }
}
