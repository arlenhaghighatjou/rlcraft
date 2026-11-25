package stanhebben.zenscript.parser.expression;

import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionBinary extends ParsedExpression {
   private final ParsedExpression left;
   private final ParsedExpression right;
   private final OperatorType operator;

   public ParsedExpressionBinary(ZenPosition position, ParsedExpression left, ParsedExpression right, OperatorType operator) {
      super(position);
      this.left = left;
      this.right = right;
      this.operator = operator;
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      Expression cLeft = this.left.compile(environment, predictedType).eval(environment);
      Expression cRight = this.right.compile(environment, predictedType).eval(environment);
      return cLeft.getType().binary(this.getPosition(), environment, cLeft, cRight, this.operator);
   }
}
