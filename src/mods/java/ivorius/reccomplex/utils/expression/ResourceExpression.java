package ivorius.reccomplex.utils.expression;

import ivorius.reccomplex.utils.RawResourceLocation;
import ivorius.reccomplex.utils.algebra.BoolFunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.RCBoolAlgebra;
import ivorius.reccomplex.utils.algebra.SupplierCache;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.text.TextFormatting;

public class ResourceExpression extends BoolFunctionExpressionCache<RawResourceLocation, Object> {
   public static final String ID_PREFIX = "id=";
   public static final String DOMAIN_PREFIX = "domain=";

   public ResourceExpression(Predicate<String> isKnown) {
      super(RCBoolAlgebra.algebra(), Boolean.valueOf(true), TextFormatting.GREEN + "Any Structure");
      this.addTypes(new ResourceExpression.ResourceIDType("id=", "", isKnown), new Function[]{(t) -> {
         return (void)t.alias("", "");
      }});
      this.addTypes(new ResourceExpression.DomainType("domain=", ""), new Function[]{(t) -> {
         return (void)t.alias("$", "");
      }});
   }

   protected static class DomainType extends FunctionExpressionCache.VariableType<Boolean, RawResourceLocation, Object> {
      public DomainType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<RawResourceLocation>, Boolean> parse(String var) {
         return (location) -> {
            return ((RawResourceLocation)location.get()).getResourceDomain().equals(var);
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object args) {
         return FunctionExpressionCache.Validity.KNOWN;
      }
   }

   protected static class ResourceIDType extends FunctionExpressionCache.VariableType<Boolean, RawResourceLocation, Object> {
      private Predicate<String> isKnown;

      public ResourceIDType(String prefix, String suffix, Predicate<String> isKnown) {
         super(prefix, suffix);
         this.isKnown = isKnown;
      }

      public Function<SupplierCache<RawResourceLocation>, Boolean> parse(String var) {
         return (location) -> {
            return ((RawResourceLocation)location.get()).getResourcePath().equals(var);
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object args) {
         return this.isKnown.test(var) ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN;
      }
   }
}
