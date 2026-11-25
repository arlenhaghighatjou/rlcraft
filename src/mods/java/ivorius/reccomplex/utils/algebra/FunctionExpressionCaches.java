package ivorius.reccomplex.utils.algebra;

import java.text.ParseException;
import java.util.function.Function;
import net.minecraft.util.text.TextFormatting;

public class FunctionExpressionCaches {
   public FunctionExpressionCaches() {
   }

   public static <T> FunctionExpressionCache.VariableType<T, Object, Object> unknown(final T val) {
      return new FunctionExpressionCache.VariableType<T, Object, Object>("", "") {
         public Function<SupplierCache<Object>, T> parse(String var) {
            return (o) -> {
               return (Function<SupplierCache<Object>, T>)val;
            };
         }

         public FunctionExpressionCache.Validity validity(String var, Object o) {
            return FunctionExpressionCache.Validity.KNOWN;
         }
      };
   }

   public static <T> FunctionExpressionCache.VariableType<T, Object, Object> constant(String id, final T val) {
      return new FunctionExpressionCache.VariableType<T, Object, Object>(id, "") {
         public Function<SupplierCache<Object>, T> parse(String var) {
            return (o) -> {
               return (Function<SupplierCache<Object>, T>)val;
            };
         }

         public FunctionExpressionCache.Validity validity(String var, Object o) {
            return var.equals(this.prefix) ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.ERROR;
         }
      };
   }

   public static String readableException(ExpressionCache<?> expressionCache) {
      ParseException parseException = expressionCache.getParseException();
      return String.format("%s%s%s: at %d", TextFormatting.RED, parseException.getMessage(), TextFormatting.RESET, parseException.getErrorOffset());
   }
}
