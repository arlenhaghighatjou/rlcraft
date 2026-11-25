package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeByte;
import stanhebben.zenscript.type.ZenTypeByteObject;
import stanhebben.zenscript.type.ZenTypeInt;
import stanhebben.zenscript.type.ZenTypeIntObject;
import stanhebben.zenscript.type.ZenTypeLong;
import stanhebben.zenscript.type.ZenTypeLongObject;
import stanhebben.zenscript.type.ZenTypeShort;
import stanhebben.zenscript.type.ZenTypeShortObject;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionInt extends Expression {
   private final long value;
   private final ZenType type;

   public ExpressionInt(ZenPosition position, long value, ZenType type) {
      super(position);
      this.value = value;
      this.type = type;
   }

   public Expression cast(ZenPosition position, IEnvironmentGlobal environment, ZenType type) {
      if (type == this.type) {
         return this;
      } else {
         switch(type.getNumberType()) {
         case 1:
         case 2:
         case 3:
         case 4:
            return new ExpressionInt(this.getPosition(), this.value, type);
         case 5:
         case 6:
            return new ExpressionFloat(this.getPosition(), (double)this.value, type);
         default:
            return super.cast(position, environment, type);
         }
      }
   }

   public ZenType getType() {
      return this.type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         MethodOutput output = environment.getOutput();
         if (this.type == ZenTypeByte.INSTANCE) {
            output.biPush((byte)((int)this.value));
         } else if (this.type == ZenTypeShort.INSTANCE) {
            output.siPush((short)((int)this.value));
         } else if (this.type == ZenTypeInt.INSTANCE) {
            output.constant(Integer.valueOf((int)this.value));
         } else if (this.type == ZenTypeLong.INSTANCE) {
            output.constant(Long.valueOf(this.value));
         } else if (this.type == ZenTypeIntObject.INSTANCE) {
            output.constant(Integer.valueOf((int)this.value));
            output.invokeStatic(ZenTypeUtil.internal(Integer.class), "valueOf", "(I)Ljava/lang/Integer;");
         } else if (this.type == ZenTypeByteObject.INSTANCE) {
            output.biPush((byte)((int)this.value));
            output.invokeStatic(ZenTypeUtil.internal(Byte.class), "valueOf", "(B)Ljava/lang/Byte;");
         } else if (this.type == ZenTypeLongObject.INSTANCE) {
            output.constant(Long.valueOf(this.value));
            output.invokeStatic(ZenTypeUtil.internal(Long.class), "valueOf", "(J)Ljava/lang/Long;");
         } else {
            if (this.type != ZenTypeShortObject.INSTANCE) {
               throw new RuntimeException("Internal compiler error: int constant type is not an int");
            }

            output.siPush((short)((int)this.value));
            output.invokeStatic(ZenTypeUtil.internal(Short.class), "valueOf", "(S)Ljava/lang/Short;");
         }

      }
   }
}
