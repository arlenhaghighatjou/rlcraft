package stanhebben.zenscript.parser.expression;

import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionCompare extends ParsedExpression {
   private final ParsedExpression left;
   private final ParsedExpression right;
   private final CompareType type;

   public ParsedExpressionCompare(ZenPosition position, ParsedExpression left, ParsedExpression right, CompareType type) {
      super(position);
      this.left = left;
      this.right = right;
      this.type = type;
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      Expression cLeft = this.left.compile(environment, (ZenType)null).eval(environment);
      Expression cRight = this.right.compile(environment, (ZenType)null).eval(environment);
      return cLeft.getType().compare(this.getPosition(), environment, cLeft, cRight, this.type);
   }
}
