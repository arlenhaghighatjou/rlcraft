package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionArraySet extends Expression {
   private final Expression array;
   private final Expression index;
   private final Expression value;

   public ExpressionArraySet(ZenPosition position, Expression array, Expression index, Expression value) {
      super(position);
      this.array = array;
      this.index = index;
      this.value = value;
   }

   public ZenType getType() {
      return ZenType.VOID;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.array.compile(result, environment);
      this.index.compile(result, environment);
      this.value.compile(result, environment);
      if (result) {
         environment.getOutput().arrayStore(this.value.getType().toASMType());
      }

   }
}
