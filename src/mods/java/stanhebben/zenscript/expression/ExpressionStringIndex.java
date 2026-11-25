package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionStringIndex extends Expression {
   private final Expression source;
   private final Expression index;

   public ExpressionStringIndex(ZenPosition position, Expression source, Expression index) {
      super(position);
      this.source = source;
      this.index = index;
   }

   public ZenType getType() {
      return ZenType.STRING;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.source.compile(result, environment);
      this.index.compile(result, environment);
      if (result) {
         environment.getOutput().dup();
         environment.getOutput().iConst1();
         environment.getOutput().iAdd();
         environment.getOutput().invokeVirtual(String.class, "substring", String.class, Integer.TYPE, Integer.TYPE);
      }

   }
}
