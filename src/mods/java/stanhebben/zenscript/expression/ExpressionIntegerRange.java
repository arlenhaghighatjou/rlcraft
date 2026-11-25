package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.value.IntRange;

public class ExpressionIntegerRange extends Expression {
   private final Expression from;
   private final Expression to;

   public ExpressionIntegerRange(ZenPosition position, Expression from, Expression to) {
      super(position);
      this.from = from;
      this.to = to;
   }

   public ZenType getType() {
      return ZenType.INTRANGE;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      MethodOutput output = environment.getOutput();
      output.newObject(IntRange.class);
      output.dup();
      this.from.compile(true, environment);
      this.to.compile(true, environment);
      output.construct(IntRange.class, Integer.TYPE, Integer.TYPE);
   }
}
