package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeDouble;
import stanhebben.zenscript.type.ZenTypeDoubleObject;
import stanhebben.zenscript.type.ZenTypeFloat;
import stanhebben.zenscript.type.ZenTypeFloatObject;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionFloat extends Expression {
   private final double value;
   private final ZenType type;

   public ExpressionFloat(ZenPosition position, double value, ZenType type) {
      super(position);
      this.value = value;
      this.type = type;
   }

   public ZenType getType() {
      return this.type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         MethodOutput output = environment.getOutput();
         if (this.type == ZenTypeFloat.INSTANCE) {
            output.constant(Float.valueOf((float)this.value));
         } else if (this.type == ZenTypeDouble.INSTANCE) {
            output.constant(Double.valueOf(this.value));
         } else if (this.type == ZenTypeFloatObject.INSTANCE) {
            output.constant(Float.valueOf((float)this.value));
            output.invokeStatic(ZenTypeUtil.internal(Float.class), "valueOf", "(F)Ljava/lang/Float;");
         } else {
            if (this.type != ZenTypeDoubleObject.INSTANCE) {
               throw new RuntimeException("Internal compiler error: source type is not a floating point type");
            }

            output.constant(Double.valueOf(this.value));
            output.invokeSpecial(ZenTypeUtil.internal(Double.class), "valueOf", "(D)Ljava/lang/Double;");
         }

      }
   }
}
