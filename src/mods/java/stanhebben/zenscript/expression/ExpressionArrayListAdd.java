package stanhebben.zenscript.expression;

import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayList;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionArrayListAdd extends Expression {
   private final Expression array;
   private final Expression value;
   private final ZenTypeArrayList type;

   public ExpressionArrayListAdd(ZenPosition position, IEnvironmentGlobal environment, Expression array, Expression val) {
      super(position);
      this.array = array;
      this.value = val;
      this.type = (ZenTypeArrayList)array.getType();
   }

   public ZenType getType() {
      return this.type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      MethodOutput output = environment.getOutput();
      this.array.compile(true, environment);
      output.dup();
      this.value.compile(true, environment);
      output.invokeInterface(List.class, "add", Boolean.TYPE, Object.class);
      output.pop();
   }
}
