package stanhebben.zenscript.parser.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionInvalid extends ParsedExpression {
   public ParsedExpressionInvalid(ZenPosition position) {
      super(position);
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      return new ExpressionInvalid(this.getPosition(), (ZenType)(predictedType == null ? ZenType.ANY : predictedType));
   }
}
