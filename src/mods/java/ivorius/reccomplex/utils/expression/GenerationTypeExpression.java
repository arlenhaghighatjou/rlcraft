package ivorius.reccomplex.utils.expression;

import ivorius.reccomplex.utils.algebra.BoolFunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.RCBoolAlgebra;
import ivorius.reccomplex.utils.algebra.SupplierCache;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import java.text.ParseException;
import java.util.function.Function;
import net.minecraft.util.text.TextFormatting;

public class GenerationTypeExpression extends BoolFunctionExpressionCache<GenerationType, Object> {
   public static final String ID_PREFIX = "id=";
   public static final String TYPE_PREFIX = "type=";

   public GenerationTypeExpression() {
      super(RCBoolAlgebra.algebra(), Boolean.valueOf(true), TextFormatting.GREEN + "Any Generation");
      this.addTypes(new GenerationTypeExpression.IdentifierType("id=", ""), new Function[0]);
      this.addTypes(new GenerationTypeExpression.TypeType("type=", ""), new Function[]{(t) -> {
         return t.alias("$", "");
      }});
   }

   protected static class IdentifierType extends FunctionExpressionCache.VariableType<Boolean, GenerationType, Object> {
      public IdentifierType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<GenerationType>, Boolean> parse(String var) throws ParseException {
         return (info) -> {
            return info.get() != null && var.equals(((GenerationType)info.get()).id());
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object args) {
         return FunctionExpressionCache.Validity.KNOWN;
      }
   }

   protected static class TypeType extends FunctionExpressionCache.VariableType<Boolean, GenerationType, Object> {
      public TypeType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<GenerationType>, Boolean> parse(String var) throws ParseException {
         Class<? extends GenerationType> theClass = StructureRegistry.GENERATION_TYPES.typeForID(var);
         return theClass != null ? (info) -> {
            return info.get() != null && theClass.isAssignableFrom(((GenerationType)info.get()).getClass());
         } : (info) -> {
            return false;
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object args) {
         return StructureRegistry.GENERATION_TYPES.typeForID(var) != null ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN;
      }
   }
}
