package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionNothing extends Expression {
   public ExpressionNothing(ZenPosition position) {
      super(position);
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
   }

   public ZenType getType() {
      return ZenType.NULL;
   }
}
