package stanhebben.zenscript.parser.expression;

import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionIndexSet extends ParsedExpression {
   private final ParsedExpression value;
   private final ParsedExpression index;
   private final ParsedExpression setValue;

   public ParsedExpressionIndexSet(ZenPosition position, ParsedExpression value, ParsedExpression index, ParsedExpression setValue) {
      super(position);
      this.value = value;
      this.index = index;
      this.setValue = setValue;
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      Expression cValue = this.value.compile(environment, (ZenType)null).eval(environment);
      Expression cIndex = this.index.compile(environment, (ZenType)null).eval(environment);
      Expression cSetValue = this.setValue.compile(environment, (ZenType)null).eval(environment);
      return cValue.getType().trinary(this.getPosition(), environment, cValue, cIndex, cSetValue, OperatorType.INDEXSET);
   }
}
