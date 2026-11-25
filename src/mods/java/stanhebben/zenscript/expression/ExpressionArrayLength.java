package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeInt;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionArrayLength extends Expression {
   private final Expression value;

   public ExpressionArrayLength(ZenPosition position, Expression value) {
      super(position);
      this.value = value;
   }

   public ZenType getType() {
      return ZenTypeInt.INSTANCE;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         this.value.compile(true, environment);
         environment.getOutput().arrayLength();
      }

   }
}
