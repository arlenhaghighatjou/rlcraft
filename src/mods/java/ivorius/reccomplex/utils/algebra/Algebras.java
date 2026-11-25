package ivorius.reccomplex.utils.algebra;

import java.text.ParseException;
import java.util.function.Function;
import javax.annotation.Nullable;

public class Algebras {
   public Algebras() {
   }

   @Nullable
   public static <T> T tryEvaluate(String expression, Algebra<T> algebra, Function<String, T> variableEvaluator) {
      variableEvaluator.getClass();
      Algebra.Expression<T, T> parsed = tryParse(expression, algebra, variableEvaluator::apply);
      return (T)(parsed != null ? parsed.evaluate((t) -> {
         return (T)t;
      }) : null);
   }

   @Nullable
   public static <T, V> Algebra.Expression<T, V> tryParse(String string, Algebra<T> algebra, Algebra.VariableParser<V> variableParser) {
      try {
         return algebra.parse(string, variableParser);
      } catch (ParseException var4) {
         return null;
      }
   }

   public abstract static class Infix<T> extends Algebra.Operator<T> {
      public Infix(float precedence, String symbol) {
         super(precedence, true, true, symbol);
      }

      public <V> T evaluate(Function<V, T> variableEvaluator, Algebra.Expression<T, V>[] expressions) {
         return (T)this.evaluate(variableEvaluator, expressions[0], expressions[1]);
      }

      public abstract <V> T evaluate(Function<V, T> var1, Algebra.Expression<T, V> var2, Algebra.Expression<T, V> var3);
   }

   public static class Parentheses<T> extends Algebra.Operator<T> {
      public Parentheses(float precedence, String open, String close) {
         super(precedence, false, false, open, close);
      }

      public <V> T evaluate(Function<V, T> variableEvaluator, Algebra.Expression<T, V>[] expressions) {
         return (T)expressions[0].evaluate(variableEvaluator);
      }
   }

   public abstract static class Unary<T> extends Algebra.Operator<T> {
      public Unary(float precedence, Algebras.Unary.Notation notation, String symbol) {
         super(precedence, notation == Algebras.Unary.Notation.POSTFIX, notation == Algebras.Unary.Notation.PREFIX, symbol);
      }

      public <V> T evaluate(Function<V, T> variableEvaluator, Algebra.Expression<T, V>[] expressions) {
         return (T)this.evaluate(variableEvaluator, expressions[0]);
      }

      public abstract <V> T evaluate(Function<V, T> var1, Algebra.Expression<T, V> var2);

      public static enum Notation {
         PREFIX,
         POSTFIX;

         private Notation() {
         }
      }
   }
}
