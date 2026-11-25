package stanhebben.zenscript.parser.expression;

import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionIndex extends ParsedExpression {
   private final ParsedExpression value;
   private final ParsedExpression index;

   public ParsedExpressionIndex(ZenPosition position, ParsedExpression value, ParsedExpression index) {
      super(position);
      this.value = value;
      this.index = index;
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      Expression cValue = this.value.compile(environment, (ZenType)null).eval(environment);
      Expression cIndex = this.index.compile(environment, (ZenType)null).eval(environment);
      return cValue.getType().binary(this.getPosition(), environment, cValue, cIndex, OperatorType.INDEXGET);
   }
}
