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

public class ExpressionArithmeticBinary extends Expression {
   private final OperatorType operator;
   private final Expression a;
   private final Expression b;

   public ExpressionArithmeticBinary(ZenPosition position, OperatorType operator, Expression a, Expression b) {
      super(position);
      this.operator = operator;
      this.a = a;
      this.b = b;
   }

   public ZenType getType() {
      return this.a.getType();
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         this.a.compile(result, environment);
         this.b.compile(result, environment);
         ZenType type = this.a.getType();
         MethodOutput output = environment.getOutput();
         if (type == ZenType.BOOL) {
            switch(this.operator) {
            case AND:
               output.iAnd();
               break;
            case OR:
               output.iOr();
               break;
            case XOR:
               output.iXor();
               break;
            default:
               throw new RuntimeException("Unsupported operator on " + type + ": " + this.operator);
            }
         } else if (type != ZenTypeByte.INSTANCE && type != ZenTypeShort.INSTANCE && type != ZenTypeInt.INSTANCE) {
            if (type == ZenTypeLong.INSTANCE) {
               switch(this.operator) {
               case AND:
                  output.lAnd();
                  break;
               case OR:
                  output.lOr();
                  break;
               case XOR:
                  output.lXor();
                  break;
               case ADD:
                  output.lAdd();
                  break;
               case SUB:
                  output.lSub();
                  break;
               case MUL:
                  output.lMul();
                  break;
               case DIV:
                  output.lDiv();
                  break;
               case MOD:
                  output.lRem();
                  break;
               default:
                  throw new RuntimeException("Unsupported operator on " + type + ": " + this.operator);
               }
            } else if (type == ZenTypeFloat.INSTANCE) {
               switch(this.operator) {
               case ADD:
                  output.fAdd();
                  break;
               case SUB:
                  output.fSub();
                  break;
               case MUL:
                  output.fMul();
                  break;
               case DIV:
                  output.fDiv();
                  break;
               case MOD:
                  output.fRem();
                  break;
               default:
                  throw new RuntimeException("Unsupported operator on " + type + ": " + this.operator);
               }
            } else {
               if (type != ZenTypeDouble.INSTANCE) {
                  throw new RuntimeException("Internal compilation error: " + type + " is not a supported arithmetic type");
               }

               switch(this.operator) {
               case ADD:
                  output.dAdd();
                  break;
               case SUB:
                  output.dSub();
                  break;
               case MUL:
                  output.dMul();
                  break;
               case DIV:
                  output.dDiv();
                  break;
               case MOD:
                  output.dRem();
                  break;
               default:
                  throw new RuntimeException("Unsupported operator on " + type + ": " + this.operator);
               }
            }
         } else {
            switch(this.operator) {
            case AND:
               output.iAnd();
               break;
            case OR:
               output.iOr();
               break;
            case XOR:
               output.iXor();
               break;
            case ADD:
               output.iAdd();
               break;
            case SUB:
               output.iSub();
               break;
            case MUL:
               output.iMul();
               break;
            case DIV:
               output.iDiv();
               break;
            case MOD:
               output.iRem();
               break;
            default:
               throw new RuntimeException("Unsupported operator on " + type + ": " + this.operator);
            }
         }
      } else {
         this.a.compile(result, environment);
         this.b.compile(result, environment);
      }

   }
}
