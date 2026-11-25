package ivorius.reccomplex.utils.algebra;

import java.util.function.Function;

public class IntAlgebra {
   public static final Function<String, Integer> CONSTANT_EVALUATOR = Integer::valueOf;

   public IntAlgebra() {
   }

   public static Algebras.Infix<Integer> plus(String symbol) {
      return new Algebras.Infix<Integer>(4.0F, symbol) {
         public <V> Integer evaluate(Function<V, Integer> variableEvaluator, Algebra.Expression<Integer, V> left, Algebra.Expression<Integer, V> right) {
            return ((Integer)left.evaluate(variableEvaluator)).intValue() + ((Integer)right.evaluate(variableEvaluator)).intValue();
         }
      };
   }

   public static Algebras.Infix<Integer> minus(String symbol) {
      return new Algebras.Infix<Integer>(4.0F, symbol) {
         public <V> Integer evaluate(Function<V, Integer> variableEvaluator, Algebra.Expression<Integer, V> left, Algebra.Expression<Integer, V> right) {
            return ((Integer)left.evaluate(variableEvaluator)).intValue() - ((Integer)right.evaluate(variableEvaluator)).intValue();
         }
      };
   }

   public static Algebras.Parentheses<Integer> parentheses(String left, String right) {
      return new Algebras.Parentheses<Integer>(1.0F, left, right);
   }
}
