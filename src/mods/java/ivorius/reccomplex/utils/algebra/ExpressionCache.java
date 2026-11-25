package ivorius.reccomplex.utils.algebra;

import java.text.ParseException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExpressionCache<T> {
   @Nonnull
   protected Algebra<T> algebra;
   protected T emptyExpressionResult;
   protected String emptyResultRepresentation;
   @Nonnull
   protected String expression = "";
   @Nullable
   protected Algebra.Expression<T, ?> parsedExpression;
   @Nullable
   protected ParseException parseException;

   public ExpressionCache(@Nonnull Algebra<T> algebra) {
      this.algebra = algebra;
      this.setExpression("");
   }

   public ExpressionCache(@Nonnull Algebra<T> algebra, T emptyExpressionResult, String emptyResultRepresentation) {
      this.algebra = algebra;
      this.emptyExpressionResult = emptyExpressionResult;
      this.emptyResultRepresentation = emptyResultRepresentation;
      this.setExpression("");
   }

   public static <T, E extends ExpressionCache<T>> E of(E e, String expression) {
      e.setExpression(expression);
      return e;
   }

   protected void parseExpression() {
      if (this.expressionIsEmpty() && this.acceptsEmptyExpression()) {
         this.parsedExpression = new Algebra.Constant(0, this.emptyExpressionResult, this.emptyResultRepresentation);
         this.parseException = null;
      } else {
         try {
            this.parsedExpression = this.algebra.parse(this.expression, this.variableParser());
            this.parseException = null;
         } catch (ParseException var2) {
            this.parsedExpression = null;
            this.parseException = var2;
         }
      }

   }

   protected Algebra.VariableParser<?> variableParser() {
      return (i) -> {
         return i;
      };
   }

   @Nonnull
   public String getExpression() {
      return this.expression;
   }

   public void setExpression(@Nonnull String expression) {
      this.expression = expression;
      this.parseExpression();
   }

   @Nonnull
   public Algebra<T> getAlgebra() {
      return this.algebra;
   }

   public void setAlgebra(@Nonnull Algebra<T> algebra) {
      this.algebra = algebra;
   }

   public T getEmptyExpressionResult() {
      return this.emptyExpressionResult;
   }

   public String getEmptyExpressionResultRepresentation() {
      return this.emptyResultRepresentation;
   }

   public void setNoEmptyExpressionResult() {
      this.emptyExpressionResult = null;
      this.emptyResultRepresentation = null;
      this.parseExpression();
   }

   public void setEmptyExpressionResult(T emptyResult, String representation) {
      this.emptyExpressionResult = emptyResult;
      this.emptyResultRepresentation = representation;
      this.parseExpression();
   }

   public boolean acceptsEmptyExpression() {
      return this.emptyResultRepresentation != null;
   }

   public boolean expressionIsEmpty() {
      return this.expression.trim().isEmpty();
   }

   @Nullable
   public Algebra.Expression<T, ?> getParsedExpression() {
      return this.parsedExpression;
   }

   @Nullable
   public ParseException getParseException() {
      return this.parseException;
   }

   public boolean isExpressionValid() {
      return this.parsedExpression != null && this.parseException == null;
   }
}
