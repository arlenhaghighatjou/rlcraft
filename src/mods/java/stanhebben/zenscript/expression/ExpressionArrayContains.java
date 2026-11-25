package stanhebben.zenscript.expression;

import java.util.Collection;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArray;
import stanhebben.zenscript.type.ZenTypeArrayList;
import stanhebben.zenscript.util.ArrayUtil;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionArrayContains extends Expression {
   private final ZenTypeArray type;
   private final Expression array;
   private final Expression toCheck;

   public ExpressionArrayContains(ZenPosition position, IEnvironmentGlobal environment, Expression array, Expression toCheck) {
      super(position);
      this.type = (ZenTypeArray)array.getType();
      this.array = array;
      this.toCheck = toCheck;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.array.compile(result, environment);
      this.toCheck.compile(result, environment);
      if (result) {
         if (this.type instanceof ZenTypeArrayList) {
            this.compileList(environment.getOutput());
         } else {
            this.compileArray(environment.getOutput());
         }

      }
   }

   public ZenType getType() {
      return ZenType.BOOL;
   }

   private final void compileList(MethodOutput output) {
      output.invokeInterface(Collection.class, "contains", Boolean.TYPE, Object.class);
   }

   private final void compileArray(MethodOutput output) {
      if (this.type.getBaseType().toJavaClass().isPrimitive()) {
         output.invokeStatic(ArrayUtil.class, "contains", Boolean.TYPE, this.type.toJavaClass(), this.type.getBaseType().toJavaClass());
      } else {
         output.invokeStatic(ArrayUtil.class, "contains", Boolean.TYPE, Object[].class, Object.class);
      }

   }
}
