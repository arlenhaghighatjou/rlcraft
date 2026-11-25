package stanhebben.zenscript.parser.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.ExpressionConditional;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionConditional extends ParsedExpression {
   private final ParsedExpression condition;
   private final ParsedExpression ifThen;
   private final ParsedExpression ifElse;

   public ParsedExpressionConditional(ZenPosition position, ParsedExpression condition, ParsedExpression ifThen, ParsedExpression ifElse) {
      super(position);
      this.condition = condition;
      this.ifThen = ifThen;
      this.ifElse = ifElse;
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      return new ExpressionConditional(this.getPosition(), this.condition.compile(environment, ZenType.BOOL).eval(environment), this.ifThen.compile(environment, predictedType).eval(environment), this.ifElse.compile(environment, predictedType).eval(environment));
   }
}
