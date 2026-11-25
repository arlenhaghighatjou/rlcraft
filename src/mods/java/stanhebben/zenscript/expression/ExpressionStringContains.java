package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionStringContains extends Expression {
   private final Expression haystack;
   private final Expression needle;

   public ExpressionStringContains(ZenPosition position, Expression haystack, Expression needle) {
      super(position);
      this.haystack = haystack;
      this.needle = needle;
   }

   public ZenType getType() {
      return ZenType.BOOL;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.haystack.compile(result, environment);
      this.needle.compile(result, environment);
      if (result) {
         environment.getOutput().invokeVirtual(String.class, "contains", Boolean.TYPE, CharSequence.class);
      }

   }
}
