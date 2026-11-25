package stanhebben.zenscript.expression;

import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeVoid;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionArrayListSet extends Expression {
   private final Expression array;
   private final Expression index;
   private final Expression value;
   private final ZenType type;
   private final ZenPosition position;

   public ExpressionArrayListSet(ZenPosition position, Expression array, Expression index, Expression value) {
      super(position);
      this.array = array;
      this.index = index;
      this.value = value;
      this.type = ZenTypeVoid.INSTANCE;
      this.position = position;
   }

   public ZenType getType() {
      return this.type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.array.compile(result, environment);
      this.index.compile(result, environment);
      this.value.compile(result, environment);
      if (result) {
         environment.getOutput().invokeInterface(List.class, "set", Object.class, Integer.TYPE, Object.class);
      }

   }
}
