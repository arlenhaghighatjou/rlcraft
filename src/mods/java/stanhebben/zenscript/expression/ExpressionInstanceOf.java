package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionInstanceOf extends Expression {
   private final Expression expression;
   private final ZenType type;

   public ExpressionInstanceOf(ZenPosition position, Expression expression, ZenType type) {
      super(position);
      this.expression = expression;
      this.type = type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         this.expression.compile(result, environment);
         environment.getOutput().instanceOf(ZenTypeUtil.internal(this.type.toJavaClass()));
      }
   }

   public ZenType getType() {
      return ZenType.BOOL;
   }
}
