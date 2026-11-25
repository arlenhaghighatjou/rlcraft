package stanhebben.zenscript.expression;

import org.objectweb.asm.Label;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionCompareGeneric extends Expression {
   private final Expression value;
   private final CompareType type;

   public ExpressionCompareGeneric(ZenPosition position, Expression value, CompareType type) {
      super(position);
      this.value = value;
      this.type = type;
   }

   public ZenType getType() {
      return ZenType.BOOL;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.value.compile(result, environment);
      if (result) {
         MethodOutput output = environment.getOutput();
         Label lblThen = new Label();
         Label lblEnd = new Label();
         switch(this.type) {
         case LT:
            output.ifLT(lblThen);
            break;
         case GT:
            output.ifGT(lblThen);
            break;
         case LE:
            output.ifLE(lblThen);
            break;
         case GE:
            output.ifGE(lblThen);
            break;
         case NE:
            output.ifNE(lblThen);
            break;
         case EQ:
            output.ifEQ(lblThen);
            break;
         default:
            throw new RuntimeException();
         }

         output.iConst0();
         output.goTo(lblEnd);
         output.label(lblThen);
         output.iConst1();
         output.label(lblEnd);
      }

   }
}
