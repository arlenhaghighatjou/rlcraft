package ivorius.reccomplex.utils.algebra;

import java.util.function.Function;

public abstract class BoolAlgebra {
   public static final Function<String, Boolean> CONSTANT_EVALUATOR = Boolean::valueOf;

   public BoolAlgebra() {
   }

   public static Algebras.Unary<Boolean> not(String symbol) {
      return new Algebras.Unary<Boolean>(5.0F, Algebras.Unary.Notation.PREFIX, symbol) {
         public <V> Boolean evaluate(Function<V, Boolean> variableEvaluator, Algebra.Expression<Boolean, V> expression) {
            return !((Boolean)expression.evaluate(variableEvaluator)).booleanValue();
         }
      };
   }

   public static Algebras.Infix<Boolean> and(String symbol) {
      return new Algebras.Infix<Boolean>(4.0F, symbol) {
         public <V> Boolean evaluate(Function<V, Boolean> variableEvaluator, Algebra.Expression<Boolean, V> left, Algebra.Expression<Boolean, V> right) {
            return ((Boolean)left.evaluate(variableEvaluator)).booleanValue() && ((Boolean)right.evaluate(variableEvaluator)).booleanValue();
         }
      };
   }

   public static Algebras.Infix<Boolean> or(String symbol) {
      return new Algebras.Infix<Boolean>(4.0F, symbol) {
         public <V> Boolean evaluate(Function<V, Boolean> variableEvaluator, Algebra.Expression<Boolean, V> left, Algebra.Expression<Boolean, V> right) {
            return ((Boolean)left.evaluate(variableEvaluator)).booleanValue() || ((Boolean)right.evaluate(variableEvaluator)).booleanValue();
         }
      };
   }

   public static Algebras.Infix<Boolean> equals(String symbol) {
      return new Algebras.Infix<Boolean>(3.0F, symbol) {
         public <V> Boolean evaluate(Function<V, Boolean> variableEvaluator, Algebra.Expression<Boolean, V> left, Algebra.Expression<Boolean, V> right) {
            return left.evaluate(variableEvaluator) == right.evaluate(variableEvaluator);
         }
      };
   }

   public static Algebras.Infix<Boolean> unEquals(String symbol) {
      return new Algebras.Infix<Boolean>(3.0F, symbol) {
         public <V> Boolean evaluate(Function<V, Boolean> variableEvaluator, Algebra.Expression<Boolean, V> left, Algebra.Expression<Boolean, V> right) {
            return left.evaluate(variableEvaluator) == right.evaluate(variableEvaluator);
         }
      };
   }

   public static Algebra.Operator<Boolean> conditional(String left, String right) {
      return new Algebra.Operator<Boolean>(2.0F, true, true, new String[]{left, right}) {
         public <V> Boolean evaluate(Function<V, Boolean> variableEvaluator, Algebra.Expression<Boolean, V>[] expressions) {
            return ((Boolean)expressions[0].evaluate(variableEvaluator)).booleanValue() ? (Boolean)expressions[1].evaluate(variableEvaluator) : (Boolean)expressions[2].evaluate(variableEvaluator);
         }
      };
   }

   public static Algebras.Parentheses<Boolean> parentheses(String left, String right) {
      return new Algebras.Parentheses<Boolean>(1.0F, left, right);
   }
}
