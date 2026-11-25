package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayBasic;
import stanhebben.zenscript.util.ArrayUtil;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionArrayAdd extends Expression {
   private final Expression array;
   private final Expression value;
   private final ZenTypeArrayBasic type;

   public ExpressionArrayAdd(ZenPosition position, IEnvironmentGlobal environment, Expression array, Expression val) {
      super(position);
      this.array = array;
      this.value = val;
      this.type = (ZenTypeArrayBasic)array.getType();
   }

   public ZenType getType() {
      return this.type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      MethodOutput output = environment.getOutput();
      this.array.compile(true, environment);
      this.value.cast(this.getPosition(), environment, this.type.getBaseType()).compile(true, environment);
      if (this.type.getBaseType().toJavaClass().isPrimitive()) {
         Class<?> arrayType = this.getType().toJavaClass();
         output.invokeStatic(ArrayUtil.class, "add", arrayType, arrayType, this.type.getBaseType().toJavaClass());
      } else {
         output.invokeStatic(ArrayUtil.class, "add", Object[].class, Object[].class, Object.class);
         output.checkCast(this.type.getSignature());
      }

   }
}
