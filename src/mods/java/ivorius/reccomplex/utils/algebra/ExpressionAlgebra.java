package ivorius.reccomplex.utils.algebra;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ExpressionAlgebra {
   public ExpressionAlgebra() {
   }

   public static <T> Algebra.Operator<Object> fromAlgebra(Algebra.Operator<T> operator, final Class<T> clazz) {
      return new Algebra.Operator<Object>(operator.precedence, operator.hasLeftArgument, operator.hasRightArgument, operator.symbols) {
         public <V> Object evaluate(Function<V, Object> variableEvaluator, Algebra.Expression<Object, V>[] expressions) {
            Object[] result = Stream.of(expressions).map((e) -> {
               return e.evaluate(variableEvaluator);
            }).toArray();
            if (!Stream.of(result).allMatch((r) -> {
               return clazz.isAssignableFrom(result.getClass());
            })) {
               throw new IllegalArgumentException();
            } else {
               return result;
            }
         }
      };
   }
}
