package stanhebben.zenscript.statements;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.parser.expression.ParsedExpressionIndexSet;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class StatementExpression extends Statement {
   private final ParsedExpression expression;

   public StatementExpression(ZenPosition position, ParsedExpression expression) {
      super(position);
      this.expression = expression;
   }

   public void compile(IEnvironmentMethod environment) {
      this.compile(environment, false);
   }

   public void compile(IEnvironmentMethod environment, boolean forced) {
      environment.getOutput().position(this.getPosition());
      boolean shouldCompile = ParsedExpressionIndexSet.class.isInstance(this.expression);
      this.expression.compile(environment, (ZenType)null).eval(environment).compile(shouldCompile || forced, environment);
   }
}
