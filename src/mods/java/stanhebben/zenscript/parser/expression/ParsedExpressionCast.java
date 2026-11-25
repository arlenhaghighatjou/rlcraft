package stanhebben.zenscript.parser.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionCast extends ParsedExpression {
   private final ParsedExpression value;
   private final ZenType type;

   public ParsedExpressionCast(ZenPosition position, ParsedExpression value, ZenType type) {
      super(position);
      this.value = value;
      this.type = type;
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      return this.value.compile(environment, this.type).eval(environment).cast(this.getPosition(), environment, this.type);
   }
}
