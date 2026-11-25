package ivorius.reccomplex.utils.expression;

import ivorius.reccomplex.utils.algebra.BoolFunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.RCBoolAlgebra;
import ivorius.reccomplex.utils.algebra.SupplierCache;
import java.text.ParseException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class PreloadedBooleanExpression<A> extends BoolFunctionExpressionCache<A, Object> {
   public PreloadedBooleanExpression() {
      this(Boolean.valueOf(true), "Any");
   }

   public PreloadedBooleanExpression(Boolean emptyResult, String emptyResultRepresentation) {
      super(RCBoolAlgebra.algebra(), emptyResult, emptyResultRepresentation);
   }

   public static <A> PreloadedBooleanExpression<A> with(Consumer<PreloadedBooleanExpression<A>> consumer) {
      PreloadedBooleanExpression<A> exp = new PreloadedBooleanExpression<A>();
      consumer.accept(exp);
      return exp;
   }

   @SafeVarargs
   public final void addConstant(String id, A... as) {
      this.addEvaluator(id, (a) -> {
         Stream var10000 = Arrays.stream(as);
         a.getClass();
         return (void)var10000.anyMatch(a::equals);
      });
   }

   @SafeVarargs
   public final void addConstants(A... as) {
      for(A a : as) {
         this.addConstant(a.toString(), a);
      }

   }

   public <B> void addEvaluators(Function<B, Predicate<A>> predicate, B... bs) {
      for(B b : bs) {
         this.addEvaluator(b.toString(), (Predicate)predicate.apply(b));
      }

   }

   public void addEvaluator(String id, final Predicate<A> predicate) {
      this.addType(new FunctionExpressionCache.VariableType<Boolean, A, Object>(id, "") {
         public Function<SupplierCache<A>, Boolean> parse(String var) throws ParseException {
            return (a) -> {
               return predicate.test(a.get());
            };
         }

         public FunctionExpressionCache.Validity validity(String var, Object o) {
            return FunctionExpressionCache.Validity.KNOWN;
         }
      });
   }
}
