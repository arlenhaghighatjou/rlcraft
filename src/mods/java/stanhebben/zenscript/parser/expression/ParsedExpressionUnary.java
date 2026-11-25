package stanhebben.zenscript.parser.expression;

import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionUnary extends ParsedExpression {
   private final ParsedExpression value;
   private final OperatorType operator;

   public ParsedExpressionUnary(ZenPosition position, ParsedExpression value, OperatorType operator) {
      super(position);
      this.value = value;
      this.operator = operator;
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      Expression cValue = this.value.compile(environment, predictedType).eval(environment);
      return cValue.getType().unary(this.getPosition(), environment, cValue, this.operator);
   }
}
