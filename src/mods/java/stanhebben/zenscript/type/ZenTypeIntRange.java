package stanhebben.zenscript.type;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.compiler.ITypeRegistry;
import stanhebben.zenscript.compiler.TypeRegistry;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallVirtual;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionNull;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;
import stanhebben.zenscript.value.IntRange;

public class ZenTypeIntRange extends ZenType {
   public static final ZenTypeIntRange INSTANCE = new ZenTypeIntRange();
   private final IJavaMethod methodFrom;
   private final IJavaMethod methodTo;

   private ZenTypeIntRange() {
      ITypeRegistry dummy = new TypeRegistry();
      this.methodFrom = JavaMethod.get(dummy, IntRange.class, "getFrom");
      this.methodTo = JavaMethod.get(dummy, IntRange.class, "getTo");
   }

   public String getAnyClassName(IEnvironmentGlobal global) {
      throw new UnsupportedOperationException("range values cannot yet be used as any value");
   }

   public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      environment.error(position, "cannot apply unary operators on int ranges");
      return new ExpressionInvalid(position);
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      environment.error(position, "cannot apply binary operators on int ranges");
      return new ExpressionInvalid(position);
   }

   public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      environment.error(position, "cannot apply ternary operators on int ranges");
      return new ExpressionInvalid(position);
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      environment.error(position, "cannot compare int ranges");
      return new ExpressionInvalid(position, BOOL);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      byte var6 = -1;
      switch(name.hashCode()) {
      case 3707:
         if (name.equals("to")) {
            var6 = 1;
         }
         break;
      case 3151786:
         if (name.equals("from")) {
            var6 = 0;
         }
      }

      switch(var6) {
      case 0:
         return new ExpressionCallVirtual(position, environment, this.methodFrom, value.eval(environment), new Expression[0]);
      case 1:
         return new ExpressionCallVirtual(position, environment, this.methodTo, value.eval(environment), new Expression[0]);
      default:
         environment.error(position, "no such member " + name + " in int range");
         return new ExpressionInvalid(position);
      }
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      environment.error(position, "int ranges don't have static members");
      return new ExpressionInvalid(position);
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      environment.error(position, "int ranges cannot be called");
      return new ExpressionInvalid(position);
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return numValues == 1 ? new ZenTypeIntRange.IntRangeIterator(methodOutput) : null;
   }

   public Class toJavaClass() {
      return IntRange.class;
   }

   public Type toASMType() {
      return Type.getType(IntRange.class);
   }

   public int getNumberType() {
      return 0;
   }

   public String getSignature() {
      return ZenTypeUtil.signature(IntRange.class);
   }

   public boolean isPointer() {
      return true;
   }

   public String getName() {
      return "stanhebben.zenscript.value.IntRange";
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionNull(position);
   }

   private class IntRangeIterator implements IZenIterator {
      private final IEnvironmentMethod method;

      public IntRangeIterator(IEnvironmentMethod methodOutput) {
         this.method = methodOutput;
      }

      public void compileStart(int[] locals) {
         MethodOutput output = this.method.getOutput();
         output.dup();
         ZenTypeIntRange.this.methodFrom.invokeVirtual(output);
         output.storeInt(locals[0]);
         ZenTypeIntRange.this.methodTo.invokeVirtual(output);
      }

      public void compilePreIterate(int[] locals, Label exit) {
      }

      public void compilePostIterate(int[] locals, Label exit, Label repeat) {
         MethodOutput output = this.method.getOutput();
         output.dup();
         output.iinc(locals[0]);
         output.loadInt(locals[0]);
         output.ifICmpGT(repeat);
         output.goTo(exit);
      }

      public void compileEnd() {
         this.method.getOutput().pop();
      }

      public ZenType getType(int i) {
         return ZenType.INT;
      }
   }
}
