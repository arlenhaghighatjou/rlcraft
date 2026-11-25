package stanhebben.zenscript.parser.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionValue extends ParsedExpression {
   private final IPartialExpression value;

   public ParsedExpressionValue(ZenPosition position, IPartialExpression value) {
      super(position);
      if (value == null) {
         throw new IllegalArgumentException("value cannot be null");
      } else {
         this.value = value;
      }
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      return this.value;
   }
}
