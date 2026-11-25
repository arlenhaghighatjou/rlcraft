package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArray;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionArrayGet extends Expression {
   private final Expression array;
   private final Expression index;
   private final ZenType baseType;

   public ExpressionArrayGet(ZenPosition position, Expression array, Expression index) {
      super(position);
      this.array = array;
      this.index = index;
      this.baseType = ((ZenTypeArray)array.getType()).getBaseType();
   }

   public ZenType getType() {
      return this.baseType;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.array.compile(result, environment);
      this.index.compile(result, environment);
      if (result) {
         environment.getOutput().arrayLoad(this.baseType.toASMType());
      }

   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      return new ExpressionArraySet(position, this.array, this.index, other);
   }
}
