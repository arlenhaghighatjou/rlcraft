package stanhebben.zenscript.type;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionArithmeticBinary;
import stanhebben.zenscript.expression.ExpressionArithmeticCompare;
import stanhebben.zenscript.expression.ExpressionArithmeticUnary;
import stanhebben.zenscript.expression.ExpressionFloat;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.casting.CastingRuleD2F;
import stanhebben.zenscript.type.casting.CastingRuleD2I;
import stanhebben.zenscript.type.casting.CastingRuleD2L;
import stanhebben.zenscript.type.casting.CastingRuleI2B;
import stanhebben.zenscript.type.casting.CastingRuleI2S;
import stanhebben.zenscript.type.casting.CastingRuleStaticMethod;
import stanhebben.zenscript.type.casting.ICastingRule;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.AnyClassWriter;
import stanhebben.zenscript.util.IAnyDefinition;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;
import stanhebben.zenscript.value.IAny;

public class ZenTypeDouble extends ZenType {
   public static final ZenTypeDouble INSTANCE = new ZenTypeDouble();
   private static final String ANY_NAME = "any/AnyDouble";
   private static final String ANY_NAME_2 = "any.AnyDouble";

   private ZenTypeDouble() {
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return null;
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      rules.registerCastingRule(BYTE, new CastingRuleI2B(new CastingRuleD2I((ICastingRule)null)));
      rules.registerCastingRule(BYTEOBJECT, new CastingRuleStaticMethod(BYTE_VALUEOF, new CastingRuleI2B(new CastingRuleD2I((ICastingRule)null))));
      rules.registerCastingRule(SHORT, new CastingRuleI2S(new CastingRuleD2I((ICastingRule)null)));
      rules.registerCastingRule(SHORTOBJECT, new CastingRuleStaticMethod(SHORT_VALUEOF, new CastingRuleI2S(new CastingRuleD2I((ICastingRule)null))));
      rules.registerCastingRule(INT, new CastingRuleD2I((ICastingRule)null));
      rules.registerCastingRule(INTOBJECT, new CastingRuleStaticMethod(INT_VALUEOF, new CastingRuleD2I((ICastingRule)null)));
      rules.registerCastingRule(LONG, new CastingRuleD2L((ICastingRule)null));
      rules.registerCastingRule(LONGOBJECT, new CastingRuleStaticMethod(LONG_VALUEOF, new CastingRuleD2L((ICastingRule)null)));
      rules.registerCastingRule(FLOAT, new CastingRuleD2F((ICastingRule)null));
      rules.registerCastingRule(FLOATOBJECT, new CastingRuleStaticMethod(FLOAT_VALUEOF, new CastingRuleD2F((ICastingRule)null)));
      rules.registerCastingRule(DOUBLEOBJECT, new CastingRuleStaticMethod(DOUBLE_VALUEOF));
      rules.registerCastingRule(STRING, new CastingRuleStaticMethod(DOUBLE_TOSTRING_STATIC));
      rules.registerCastingRule(ANY, new CastingRuleStaticMethod(JavaMethod.getStatic(this.getAnyClassName(environment), "valueOf", ANY, DOUBLE)));
      if (followCasters) {
         this.constructExpansionCastingRules(environment, rules);
      }

   }

   public Type toASMType() {
      return Type.DOUBLE_TYPE;
   }

   public Class toJavaClass() {
      return Double.TYPE;
   }

   public int getNumberType() {
      return 6;
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      IPartialExpression result = this.memberExpansion(position, environment, value.eval(environment), name);
      if (result == null) {
         environment.error(position, "double value has no members");
         return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
      } else {
         return result;
      }
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return null;
   }

   public String getSignature() {
      return "D";
   }

   public boolean isPointer() {
      return false;
   }

   public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      return new ExpressionArithmeticUnary(position, operator, value);
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      return (Expression)(operator == OperatorType.CAT ? STRING.binary(position, environment, left.cast(position, environment, STRING), right.cast(position, environment, STRING), OperatorType.CAT) : new ExpressionArithmeticBinary(position, operator, left, right.cast(position, environment, this)));
   }

   public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      environment.error(position, "double doesn't support this operation");
      return new ExpressionInvalid(position);
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      return new ExpressionArithmeticCompare(position, type, left, right.cast(position, environment, this));
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      environment.error(position, "cannot call a double value");
      return new ExpressionInvalid(position);
   }

   public String getName() {
      return "double";
   }

   public String getAnyClassName(IEnvironmentGlobal environment) {
      if (!environment.containsClass("any.AnyDouble")) {
         environment.putClass("any.AnyDouble", new byte[0]);
         environment.putClass("any.AnyDouble", AnyClassWriter.construct(new ZenTypeDouble.AnyDefinitionDouble(environment), "any/AnyDouble", Type.DOUBLE_TYPE));
      }

      return "any/AnyDouble";
   }

   public boolean isLarge() {
      return true;
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionFloat(position, 0.0D, DOUBLE);
   }

   private class AnyDefinitionDouble implements IAnyDefinition {
      private final IEnvironmentGlobal environment;

      public AnyDefinitionDouble(IEnvironmentGlobal environment) {
         this.environment = environment;
      }

      public void defineMembers(ClassVisitor output) {
         output.visitField(2, "value", "D", (String)null, (Object)null);
         MethodOutput valueOf = new MethodOutput(output, 9, "valueOf", "(D)" + ZenTypeUtil.signature(IAny.class), (String)null, (String[])null);
         valueOf.start();
         valueOf.newObject("any/AnyDouble");
         valueOf.dup();
         valueOf.load(Type.DOUBLE_TYPE, 0);
         valueOf.construct("any/AnyDouble", "D");
         valueOf.returnObject();
         valueOf.end();
         MethodOutput constructor = new MethodOutput(output, 1, "<init>", "(D)V", (String)null, (String[])null);
         constructor.start();
         constructor.loadObject(0);
         constructor.invokeSpecial(ZenTypeUtil.internal(Object.class), "<init>", "()V");
         constructor.loadObject(0);
         constructor.load(Type.DOUBLE_TYPE, 1);
         constructor.putField("any/AnyDouble", "value", "D");
         constructor.returnType(Type.VOID_TYPE);
         constructor.end();
      }

      public void defineStaticCanCastImplicit(MethodOutput output) {
         Label lblCan = new Label();
         output.constant(Type.BYTE_TYPE);
         output.loadObject(0);
         output.ifACmpEq(lblCan);
         output.constant(Type.SHORT_TYPE);
         output.loadObject(0);
         output.ifACmpEq(lblCan);
         output.constant(Type.INT_TYPE);
         output.loadObject(0);
         output.ifACmpEq(lblCan);
         output.constant(Type.LONG_TYPE);
         output.loadObject(0);
         output.ifACmpEq(lblCan);
         output.constant(Type.FLOAT_TYPE);
         output.loadObject(0);
         output.ifACmpEq(lblCan);
         output.constant(Type.DOUBLE_TYPE);
         output.loadObject(0);
         output.ifACmpEq(lblCan);
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeDouble.this.getName());
         if (expansion != null) {
            expansion.compileAnyCanCastImplicit(ZenType.FLOAT, output, this.environment, 0);
         }

         output.iConst0();
         output.returnInt();
         output.label(lblCan);
         output.iConst1();
         output.returnInt();
      }

      public void defineStaticAs(MethodOutput output) {
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeDouble.this.getName());
         if (expansion != null) {
            expansion.compileAnyCast(ZenType.DOUBLE, output, this.environment, 0, 1);
         }

         AnyClassWriter.throwCastException(output, "double", 1);
      }

      public void defineNot(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "double", "not");
      }

      public void defineNeg(MethodOutput output) {
         output.newObject("any/AnyDouble");
         output.dup();
         this.getValue(output);
         output.dNeg();
         output.invokeSpecial("any/AnyDouble", "<init>", "(D)V");
         output.returnObject();
      }

      public void defineAdd(MethodOutput output) {
         output.newObject("any/AnyDouble");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASDOUBLE.invokeVirtual(output);
         output.dAdd();
         output.invokeSpecial("any/AnyDouble", "<init>", "(D)V");
         output.returnObject();
      }

      public void defineSub(MethodOutput output) {
         output.newObject("any/AnyDouble");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASDOUBLE.invokeVirtual(output);
         output.dSub();
         output.invokeSpecial("any/AnyDouble", "<init>", "(D)V");
         output.returnObject();
      }

      public void defineCat(MethodOutput output) {
         output.newObject(StringBuilder.class);
         output.dup();
         output.invokeSpecial(ZenTypeUtil.internal(StringBuilder.class), "<init>", "()V");
         this.getValue(output);
         output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, Double.TYPE);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASSTRING.invokeVirtual(output);
         output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, String.class);
         output.invokeVirtual(StringBuilder.class, "toString", String.class);
         output.invokeStatic(ZenType.STRING.getAnyClassName(this.environment), "valueOf", "(Ljava/lang/String;)" + ZenTypeUtil.signature(IAny.class));
         output.returnObject();
      }

      public void defineMul(MethodOutput output) {
         output.newObject("any/AnyDouble");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASDOUBLE.invokeVirtual(output);
         output.dMul();
         output.invokeSpecial("any/AnyDouble", "<init>", "(D)V");
         output.returnObject();
      }

      public void defineDiv(MethodOutput output) {
         output.newObject("any/AnyDouble");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASDOUBLE.invokeVirtual(output);
         output.dDiv();
         output.invokeSpecial("any/AnyDouble", "<init>", "(D)V");
         output.returnObject();
      }

      public void defineMod(MethodOutput output) {
         output.newObject("any/AnyDouble");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASDOUBLE.invokeVirtual(output);
         output.dRem();
         output.invokeSpecial("any/AnyDouble", "<init>", "(D)V");
         output.returnObject();
      }

      public void defineAnd(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "double", "and");
      }

      public void defineOr(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "double", "or");
      }

      public void defineXor(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "double", "xor");
      }

      public void defineRange(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "double", "range");
      }

      public void defineCompareTo(MethodOutput output) {
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASDOUBLE.invokeVirtual(output);
         output.invokeStatic(Float.class, "compare", Integer.TYPE, Double.TYPE, Double.TYPE);
         output.returnInt();
      }

      public void defineContains(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "double", "in");
      }

      public void defineMemberGet(MethodOutput output) {
         output.aConstNull();
         output.returnObject();
      }

      public void defineMemberSet(MethodOutput output) {
         output.returnType(Type.VOID_TYPE);
      }

      public void defineMemberCall(MethodOutput output) {
         output.aConstNull();
         output.returnObject();
      }

      public void defineIndexGet(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "double", "get []");
      }

      public void defineIndexSet(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "double", "set []");
      }

      public void defineCall(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "double", "call");
      }

      public void defineAsBool(MethodOutput output) {
         AnyClassWriter.throwCastException(output, "any/AnyDouble", "bool");
      }

      public void defineAsByte(MethodOutput output) {
         this.getValue(output);
         output.d2i();
         output.i2b();
         output.returnType(Type.BYTE_TYPE);
      }

      public void defineAsShort(MethodOutput output) {
         this.getValue(output);
         output.d2i();
         output.i2s();
         output.returnType(Type.SHORT_TYPE);
      }

      public void defineAsInt(MethodOutput output) {
         this.getValue(output);
         output.d2i();
         output.returnType(Type.INT_TYPE);
      }

      public void defineAsLong(MethodOutput output) {
         this.getValue(output);
         output.d2l();
         output.returnType(Type.LONG_TYPE);
      }

      public void defineAsFloat(MethodOutput output) {
         this.getValue(output);
         output.d2f();
         output.returnType(Type.FLOAT_TYPE);
      }

      public void defineAsDouble(MethodOutput output) {
         this.getValue(output);
         output.returnType(Type.DOUBLE_TYPE);
      }

      public void defineAsString(MethodOutput output) {
         this.getValue(output);
         output.invokeStatic(Double.class, "toString", String.class, Double.TYPE);
         output.returnObject();
      }

      public void defineAs(MethodOutput output) {
         int localValue = output.local(Type.DOUBLE_TYPE);
         this.getValue(output);
         output.store(Type.DOUBLE_TYPE, localValue);
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeDouble.this.getName());
         if (expansion != null) {
            expansion.compileAnyCast(ZenType.DOUBLE, output, this.environment, localValue, 1);
         }

         AnyClassWriter.throwCastException(output, "double", 1);
      }

      public void defineIs(MethodOutput output) {
         Label lblEq = new Label();
         output.loadObject(1);
         output.constant(Type.DOUBLE_TYPE);
         output.ifACmpEq(lblEq);
         output.iConst0();
         output.returnInt();
         output.label(lblEq);
         output.iConst1();
         output.returnInt();
      }

      public void defineGetNumberType(MethodOutput output) {
         output.constant(Integer.valueOf(6));
         output.returnInt();
      }

      public void defineIteratorSingle(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "double", "iterator");
      }

      public void defineIteratorMulti(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "double", "iterator");
      }

      private void getValue(MethodOutput output) {
         output.loadObject(0);
         output.getField("any/AnyDouble", "value", "D");
      }

      public void defineEquals(MethodOutput output) {
         Label lblNope = new Label();
         output.loadObject(1);
         output.instanceOf("stanhebben/zenscript/value/IAny");
         output.ifEQ(lblNope);
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASDOUBLE.invokeVirtual(output);
         output.dCmp();
         output.ifNE(lblNope);
         output.iConst1();
         output.returnInt();
         output.label(lblNope);
         output.iConst0();
         output.returnInt();
      }

      public void defineHashCode(MethodOutput output) {
         int bits = output.local(Type.LONG_TYPE);
         output.invokeStatic(Double.class, "doubleToLongBits", Double.TYPE, Long.TYPE);
         output.store(Type.LONG_TYPE, bits);
         output.load(Type.LONG_TYPE, bits);
         output.l2i();
         output.load(Type.LONG_TYPE, bits);
         output.constant(Integer.valueOf(32));
         output.lShr();
         output.l2i();
         output.iXor();
         output.returnInt();
      }
   }
}
