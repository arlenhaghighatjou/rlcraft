package ivorius.reccomplex.utils.expression;

import ivorius.reccomplex.files.loading.LeveledRegistry;
import ivorius.reccomplex.utils.algebra.BoolFunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.RCBoolAlgebra;
import ivorius.reccomplex.utils.algebra.SupplierCache;
import java.text.ParseException;
import java.util.function.Function;
import net.minecraft.util.text.TextFormatting;

public class RegistryExpression extends BoolFunctionExpressionCache<LeveledRegistry, LeveledRegistry> {
   public static final String HAS_PREFIX = "has:";

   public RegistryExpression() {
      super(RCBoolAlgebra.algebra(), Boolean.valueOf(true), TextFormatting.GREEN + "Any");
      this.addTypes(new RegistryExpression.RegistryVariableType("has:", ""), new Function[]{(t) -> {
         return t.alias("#", "");
      }});
   }

   protected static class RegistryVariableType extends FunctionExpressionCache.VariableType<Boolean, LeveledRegistry, LeveledRegistry> {
      public RegistryVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<LeveledRegistry>, Boolean> parse(String var) throws ParseException {
         return (registry) -> {
            return ((LeveledRegistry)registry.get()).has(var);
         };
      }

      public FunctionExpressionCache.Validity validity(String var, LeveledRegistry registry) {
         return registry.has(var) ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN;
      }
   }
}
