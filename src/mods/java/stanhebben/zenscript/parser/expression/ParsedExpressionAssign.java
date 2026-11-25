package stanhebben.zenscript.parser.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionAssign extends ParsedExpression {
   private final ParsedExpression left;
   private final ParsedExpression right;

   public ParsedExpressionAssign(ZenPosition position, ParsedExpression left, ParsedExpression right) {
      super(position);
      this.left = left;
      this.right = right;
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      IPartialExpression cLeft = this.left.compile(environment, predictedType);
      return cLeft.assign(this.getPosition(), environment, this.right.compile(environment, cLeft.getType()).eval(environment));
   }
}
