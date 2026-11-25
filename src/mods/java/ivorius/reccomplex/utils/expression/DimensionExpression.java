package ivorius.reccomplex.utils.expression;

import com.google.common.primitives.Ints;
import ivorius.reccomplex.dimensions.DimensionDictionary;
import ivorius.reccomplex.utils.algebra.BoolFunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.RCBoolAlgebra;
import ivorius.reccomplex.utils.algebra.SupplierCache;
import java.util.function.Function;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.ArrayUtils;

public class DimensionExpression extends BoolFunctionExpressionCache<WorldProvider, Object> {
   public static final String DIMENSION_ID_PREFIX = "id=";
   public static final String DIMENSION_TYPE_PREFIX = "type=";

   public DimensionExpression() {
      super(RCBoolAlgebra.algebra(), Boolean.valueOf(true), TextFormatting.GREEN + "Any Dimension");
      this.addTypes(new DimensionExpression.DimensionVariableType("id=", ""), new Function[]{(t) -> {
         return t.alias("", "");
      }});
      this.addTypes(new DimensionExpression.DimensionDictVariableType("type=", ""), new Function[]{(t) -> {
         return t.alias("$", "");
      }});
   }

   public static String ofTypes(String... dimensionTypes) {
      return "type=" + String.join(" & type=", dimensionTypes);
   }

   protected class DimensionDictVariableType extends FunctionExpressionCache.VariableType<Boolean, WorldProvider, Object> {
      public DimensionDictVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<WorldProvider>, Boolean> parse(String var) {
         return (provider) -> {
            return DimensionDictionary.dimensionMatchesType((WorldProvider)provider.get(), var);
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object args) {
         return DimensionDictionary.allRegisteredTypes().contains(var) ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN;
      }
   }

   protected class DimensionVariableType extends FunctionExpressionCache.VariableType<Boolean, WorldProvider, Object> {
      public DimensionVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<WorldProvider>, Boolean> parse(String var) {
         Integer dimID = Ints.tryParse(var);
         return (provider) -> {
            return dimID != null && ((WorldProvider)provider.get()).getDimension() == dimID.intValue();
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object args) {
         Integer dimID = Ints.tryParse(var);
         return dimID != null && ArrayUtils.contains(DimensionManager.getIDs(), dimID) ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN;
      }
   }
}
