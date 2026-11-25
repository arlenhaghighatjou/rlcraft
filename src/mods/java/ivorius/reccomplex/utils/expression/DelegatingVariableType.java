package ivorius.reccomplex.utils.expression;

import ivorius.reccomplex.utils.algebra.ExpressionCache;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.SupplierCache;
import java.text.ParseException;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.text.TextFormatting;

public abstract class DelegatingVariableType<T, A, U, CA, CU, C extends FunctionExpressionCache<T, CA, CU>> extends FunctionExpressionCache.VariableType<T, A, U> {
   protected C cache;

   public DelegatingVariableType(String prefix, String suffix) {
      super(prefix, suffix);
   }

   protected C cache() {
      return (C)(this.cache != null ? this.cache : (this.cache = this.createCache()));
   }

   public CA convertEvaluateArgument(String var, A a) {
      return (CA)this.convertArgument(var, a);
   }

   public CU convertIsKnownArgument(String var, U u) {
      return (CU)this.convertArgument(var, u);
   }

   public CA convertArgument(String var, A a) {
      return (CA)null;
   }

   public Function<SupplierCache<A>, T> parse(String var) throws ParseException {
      C c = ExpressionCache.of(this.createCache(), var);
      return (a) -> {
         return (Function<SupplierCache<A>, T>)c.evaluate(() -> {
            return (Function<SupplierCache<A>, T>)this.convertEvaluateArgument(var, a.get());
         });
      };
   }

   public FunctionExpressionCache.Validity validity(String var, U u) {
      return this.cache().variableValidity(var, this.convertIsKnownArgument(var, u));
   }

   protected String getVarRepresentation(String var, U u) {
      return this.cache().variableDisplayString(var, this.convertIsKnownArgument(var, u));
   }

   public String getRepresentation(String var, String prefix, String suffix, U u) {
      int prefixSymbolStart = prefix.length() > 0 ? prefix.length() - 1 : 0;
      return TextFormatting.BLUE + prefix.substring(0, prefixSymbolStart) + TextFormatting.YELLOW + prefix.substring(prefixSymbolStart) + TextFormatting.RESET + this.getVarRepresentation(var, u) + TextFormatting.RESET + TextFormatting.BLUE + suffix;
   }

   public abstract C createCache();
}
