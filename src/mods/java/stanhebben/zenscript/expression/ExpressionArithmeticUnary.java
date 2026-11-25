package stanhebben.zenscript.expression;

import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeByte;
import stanhebben.zenscript.type.ZenTypeDouble;
import stanhebben.zenscript.type.ZenTypeFloat;
import stanhebben.zenscript.type.ZenTypeInt;
import stanhebben.zenscript.type.ZenTypeLong;
import stanhebben.zenscript.type.ZenTypeShort;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionArithmeticUnary extends Expression {
   private final Expression base;
   private final OperatorType operator;

   public ExpressionArithmeticUnary(ZenPosition position, OperatorType operator, Expression base) {
      super(position);
      this.base = base;
      this.operator = operator;
   }

   public ZenType getType() {
      return this.base.getType();
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.base.compile(result, environment);
      MethodOutput output = environment.getOutput();
      if (result) {
         ZenType type = this.base.getType();
         if (type == ZenType.BOOL) {
            if (this.operator == OperatorType.NOT) {
               output.iXorVs1();
               return;
            }
         } else if (type != ZenTypeByte.INSTANCE && type != ZenTypeShort.INSTANCE && type != ZenTypeInt.INSTANCE) {
            if (type == ZenTypeLong.INSTANCE) {
               if (this.operator == OperatorType.NOT) {
                  output.lNot();
                  return;
               }

               if (this.operator == OperatorType.NEG) {
                  output.lNeg();
                  return;
               }
            } else if (type == ZenTypeFloat.INSTANCE) {
               if (this.operator == OperatorType.NEG) {
                  output.fNeg();
                  return;
               }
            } else if (type == ZenTypeDouble.INSTANCE && this.operator == OperatorType.NEG) {
               output.dNeg();
               return;
            }
         } else {
            if (this.operator == OperatorType.NOT) {
               output.iNot();
               return;
            }

            if (this.operator == OperatorType.NEG) {
               output.iNeg();
               return;
            }
         }
      }

      environment.error(this.getPosition(), "Invalid operation");
   }
}
