package stanhebben.zenscript.parser.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.ExpressionInstanceOf;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionInstanceOf extends ParsedExpression {
   private final ParsedExpression base;
   private final ZenType type;

   public ParsedExpressionInstanceOf(ZenPosition position, ParsedExpression base, ZenType type) {
      super(position);
      this.base = base;
      this.type = type;
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      IPartialExpression ex = this.base.compile(environment, (ZenType)null);
      return new ExpressionInstanceOf(this.getPosition(), ex.eval(environment), this.type);
   }
}
