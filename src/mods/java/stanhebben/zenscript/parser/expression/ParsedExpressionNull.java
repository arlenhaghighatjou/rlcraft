package stanhebben.zenscript.parser.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.ExpressionNull;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionNull extends ParsedExpression {
   public ParsedExpressionNull(ZenPosition position) {
      super(position);
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      return new ExpressionNull(this.getPosition());
   }
}
