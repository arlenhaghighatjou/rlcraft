package stanhebben.zenscript.expression;

import org.objectweb.asm.Label;
import stanhebben.zenscript.annotations.CompareType;
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

public class ExpressionArithmeticCompare extends Expression {
   private final Expression a;
   private final Expression b;
   private final CompareType type;

   public ExpressionArithmeticCompare(ZenPosition position, CompareType type, Expression a, Expression b) {
      super(position);
      this.a = a;
      this.b = b;
      this.type = type;
   }

   public ZenType getType() {
      return ZenType.BOOL;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.a.compile(result, environment);
      this.b.compile(result, environment);
      if (result) {
         MethodOutput output = environment.getOutput();
         if (this.a.getType() == ZenType.BOOL) {
            if (this.type == CompareType.EQ) {
               Label onThen = new Label();
               Label onEnd = new Label();
               output.ifICmpEQ(onThen);
               output.iConst0();
               output.goTo(onEnd);
               output.label(onThen);
               output.iConst1();
               output.label(onEnd);
            } else if (this.type == CompareType.NE) {
               Label onThen = new Label();
               Label onEnd = new Label();
               output.ifICmpNE(onThen);
               output.iConst0();
               output.goTo(onEnd);
               output.label(onThen);
               output.iConst1();
               output.label(onEnd);
            } else {
               environment.error(this.getPosition(), "this kind of comparison is not supported on bool values");
            }
         } else {
            Label onThen = new Label();
            Label onEnd = new Label();
            if (this.a.getType() == ZenTypeLong.INSTANCE) {
               output.lCmp();
               output.iConst0();
            } else if (this.a.getType() == ZenTypeFloat.INSTANCE) {
               output.fCmp();
               output.iConst0();
            } else if (this.a.getType() == ZenTypeDouble.INSTANCE) {
               output.dCmp();
               output.iConst0();
            } else if (this.a.getType() != ZenTypeByte.INSTANCE && this.a.getType() != ZenTypeShort.INSTANCE && this.a.getType() != ZenTypeInt.INSTANCE) {
               throw new RuntimeException("Unsupported type for arithmetic compare");
            }

            switch(this.type) {
            case EQ:
               output.ifICmpEQ(onThen);
               break;
            case NE:
               output.ifICmpNE(onThen);
               break;
            case LE:
               output.ifICmpLE(onThen);
               break;
            case GE:
               output.ifICmpGE(onThen);
               break;
            case LT:
               output.ifICmpLT(onThen);
               break;
            case GT:
               output.ifICmpGT(onThen);
               break;
            default:
               environment.error(this.getPosition(), "this kind of comparison is not supported on int values");
               return;
            }

            output.iConst0();
            output.goTo(onEnd);
            output.label(onThen);
            output.iConst1();
            output.label(onEnd);
         }
      }

   }
}
