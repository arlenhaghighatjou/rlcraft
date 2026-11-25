package ivorius.reccomplex.utils.expression;

import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.SupplierCache;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.registry.RegistryNamespaced;

public class Expressions {
   public Expressions() {
   }

   public static PreloadedBooleanExpression<EnumFacing> direction() {
      return PreloadedBooleanExpression.<EnumFacing>with((exp) -> {
         exp.addConstants(EnumFacing.values());
         exp.addEvaluators((axis) -> {
            return (facing) -> {
               return facing.getAxis() == axis;
            };
         }, Axis.values());
         exp.addEvaluator("horizontal", (f) -> {
            return f.getHorizontalIndex() >= 0;
         });
         exp.addEvaluator("vertical", (f) -> {
            return f.getHorizontalIndex() < 0;
         });
      });
   }

   public static List<EnumFacing> directions(PreloadedBooleanExpression<EnumFacing> facingExpression) {
      return (List)Arrays.stream(EnumFacing.values()).filter(facingExpression).collect(Collectors.toList());
   }

   public static <T> FunctionExpressionCache.VariableType<Boolean, T, Object> dictionaryVariableType(String prefix, String suffix, final Function<String, T> converter, final Predicate<String> known) {
      return new FunctionExpressionCache.VariableType<Boolean, T, Object>(prefix, suffix) {
         public Function<SupplierCache<T>, Boolean> parse(String var) throws ParseException {
            T t = (T)converter.apply(var);
            return t != null ? (v) -> {
               return v.get() == t;
            } : (b) -> {
               return false;
            };
         }

         public FunctionExpressionCache.Validity validity(String var, Object o) {
            return known.test(var) ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN;
         }
      };
   }

   public static <T> FunctionExpressionCache.VariableType<Boolean, T, Object> registryVariableType(String prefix, String suffix, RegistryNamespaced<ResourceLocation, T> registry) {
      return dictionaryVariableType(prefix, suffix, (s) -> {
         return (FunctionExpressionCache.VariableType<Boolean, T, Object>)registry.getObject(new ResourceLocation(s));
      }, (s) -> {
         return registry.containsKey(new ResourceLocation(s));
      });
   }
}
