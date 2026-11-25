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
import stanhebben.zenscript.type.casting.CastingRuleF2D;
import stanhebben.zenscript.type.casting.CastingRuleF2I;
import stanhebben.zenscript.type.casting.CastingRuleF2L;
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

public class ZenTypeFloat extends ZenType {
   public static final ZenTypeFloat INSTANCE = new ZenTypeFloat();
   private static final String ANY_NAME = "any/AnyFloat";
   private static final String ANY_NAME_2 = "any.AnyFloat";

   private ZenTypeFloat() {
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return null;
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      rules.registerCastingRule(BYTE, new CastingRuleI2B(new CastingRuleF2I((ICastingRule)null)));
      rules.registerCastingRule(BYTEOBJECT, new CastingRuleStaticMethod(BYTE_VALUEOF, new CastingRuleI2B(new CastingRuleF2I((ICastingRule)null))));
      rules.registerCastingRule(SHORT, new CastingRuleI2S(new CastingRuleF2I((ICastingRule)null)));
      rules.registerCastingRule(SHORTOBJECT, new CastingRuleStaticMethod(SHORT_VALUEOF, new CastingRuleI2S(new CastingRuleF2I((ICastingRule)null))));
      rules.registerCastingRule(INT, new CastingRuleF2I((ICastingRule)null));
      rules.registerCastingRule(INTOBJECT, new CastingRuleStaticMethod(INT_VALUEOF, new CastingRuleF2I((ICastingRule)null)));
      rules.registerCastingRule(LONG, new CastingRuleF2L((ICastingRule)null));
      rules.registerCastingRule(LONGOBJECT, new CastingRuleStaticMethod(LONG_VALUEOF, new CastingRuleF2L((ICastingRule)null)));
      rules.registerCastingRule(FLOATOBJECT, new CastingRuleStaticMethod(FLOAT_VALUEOF));
      rules.registerCastingRule(DOUBLE, new CastingRuleF2D((ICastingRule)null));
      rules.registerCastingRule(DOUBLEOBJECT, new CastingRuleStaticMethod(DOUBLE_VALUEOF, new CastingRuleF2D((ICastingRule)null)));
      rules.registerCastingRule(STRING, new CastingRuleStaticMethod(FLOAT_TOSTRING_STATIC));
      rules.registerCastingRule(ANY, new CastingRuleStaticMethod(JavaMethod.getStatic(this.getAnyClassName(environment), "valueOf", ANY, FLOAT)));
      if (followCasters) {
         this.constructExpansionCastingRules(environment, rules);
      }

   }

   public Type toASMType() {
      return Type.FLOAT_TYPE;
   }

   public int getNumberType() {
      return 5;
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      IPartialExpression result = this.memberExpansion(position, environment, value.eval(environment), name);
      if (result == null) {
         environment.error(position, "float value has no members");
         return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
      } else {
         return result;
      }
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return null;
   }

   public String getSignature() {
      return "F";
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
      environment.error(position, "float doesn't support this operation");
      return new ExpressionInvalid(position);
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      return new ExpressionArithmeticCompare(position, type, left, right.cast(position, environment, this));
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      environment.error(position, "cannot call a float value");
      return new ExpressionInvalid(position);
   }

   public Class toJavaClass() {
      return Float.TYPE;
   }

   public String getName() {
      return "float";
   }

   public String getAnyClassName(IEnvironmentGlobal environment) {
      if (!environment.containsClass("any.AnyFloat")) {
         environment.putClass("any.AnyFloat", new byte[0]);
         environment.putClass("any.AnyFloat", AnyClassWriter.construct(new ZenTypeFloat.AnyDefinitionFloat(environment), "any/AnyFloat", Type.FLOAT_TYPE));
      }

      return "any/AnyFloat";
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionFloat(position, 0.0D, FLOAT);
   }

   private class AnyDefinitionFloat implements IAnyDefinition {
      private final IEnvironmentGlobal environment;

      public AnyDefinitionFloat(IEnvironmentGlobal environment) {
         this.environment = environment;
      }

      public void defineMembers(ClassVisitor output) {
         output.visitField(2, "value", "F", (String)null, (Object)null);
         MethodOutput valueOf = new MethodOutput(output, 9, "valueOf", "(F)" + ZenTypeUtil.signature(IAny.class), (String)null, (String[])null);
         valueOf.start();
         valueOf.newObject("any/AnyFloat");
         valueOf.dup();
         valueOf.load(Type.FLOAT_TYPE, 0);
         valueOf.construct("any/AnyFloat", "F");
         valueOf.returnObject();
         valueOf.end();
         MethodOutput constructor = new MethodOutput(output, 1, "<init>", "(F)V", (String)null, (String[])null);
         constructor.start();
         constructor.loadObject(0);
         constructor.invokeSpecial(ZenTypeUtil.internal(Object.class), "<init>", "()V");
         constructor.loadObject(0);
         constructor.load(Type.FLOAT_TYPE, 1);
         constructor.putField("any/AnyFloat", "value", "F");
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
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeFloat.this.getName());
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
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeFloat.this.getName());
         if (expansion != null) {
            expansion.compileAnyCast(ZenType.FLOAT, output, this.environment, 0, 1);
         }

         AnyClassWriter.throwCastException(output, "float", 1);
      }

      public void defineNot(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "float", "not");
      }

      public void defineNeg(MethodOutput output) {
         output.newObject("any/AnyFloat");
         output.dup();
         this.getValue(output);
         output.fNeg();
         output.invokeSpecial("any/AnyFloat", "<init>", "(F)V");
         output.returnObject();
      }

      public void defineAdd(MethodOutput output) {
         output.newObject("any/AnyFloat");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASFLOAT.invokeVirtual(output);
         output.fAdd();
         output.invokeSpecial("any/AnyFloat", "<init>", "(F)V");
         output.returnObject();
      }

      public void defineSub(MethodOutput output) {
         output.newObject("any/AnyFloat");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASFLOAT.invokeVirtual(output);
         output.fSub();
         output.invokeSpecial("any/AnyFloat", "<init>", "(F)V");
         output.returnObject();
      }

      public void defineCat(MethodOutput output) {
         output.newObject(StringBuilder.class);
         output.dup();
         output.invokeSpecial(ZenTypeUtil.internal(StringBuilder.class), "<init>", "()V");
         this.getValue(output);
         output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, Float.TYPE);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASSTRING.invokeVirtual(output);
         output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, String.class);
         output.invokeVirtual(StringBuilder.class, "toString", String.class);
         output.invokeStatic(ZenType.STRING.getAnyClassName(this.environment), "valueOf", "(Ljava/lang/String;)" + ZenTypeUtil.signature(IAny.class));
         output.returnObject();
      }

      public void defineMul(MethodOutput output) {
         output.newObject("any/AnyFloat");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASFLOAT.invokeVirtual(output);
         output.fMul();
         output.invokeSpecial("any/AnyFloat", "<init>", "(F)V");
         output.returnObject();
      }

      public void defineDiv(MethodOutput output) {
         output.newObject("any/AnyFloat");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASFLOAT.invokeVirtual(output);
         output.fDiv();
         output.invokeSpecial("any/AnyFloat", "<init>", "(F)V");
         output.returnObject();
      }

      public void defineMod(MethodOutput output) {
         output.newObject("any/AnyFloat");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASFLOAT.invokeVirtual(output);
         output.fRem();
         output.invokeSpecial("any/AnyFloat", "<init>", "(F)V");
         output.returnObject();
      }

      public void defineAnd(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "float", "and");
      }

      public void defineOr(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "float", "or");
      }

      public void defineXor(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "float", "xor");
      }

      public void defineRange(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "float", "range");
      }

      public void defineCompareTo(MethodOutput output) {
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASFLOAT.invokeVirtual(output);
         output.invokeStatic(Float.class, "compare", Integer.TYPE, Float.TYPE, Float.TYPE);
         output.returnInt();
      }

      public void defineContains(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "float", "in");
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
         AnyClassWriter.throwUnsupportedException(output, "float", "get []");
      }

      public void defineIndexSet(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "float", "set []");
      }

      public void defineCall(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "float", "call");
      }

      public void defineAsBool(MethodOutput output) {
         AnyClassWriter.throwCastException(output, "any/AnyFloat", "bool");
      }

      public void defineAsByte(MethodOutput output) {
         this.getValue(output);
         output.f2i();
         output.i2b();
         output.returnType(Type.BYTE_TYPE);
      }

      public void defineAsShort(MethodOutput output) {
         this.getValue(output);
         output.f2i();
         output.i2s();
         output.returnType(Type.SHORT_TYPE);
      }

      public void defineAsInt(MethodOutput output) {
         this.getValue(output);
         output.f2i();
         output.returnType(Type.INT_TYPE);
      }

      public void defineAsLong(MethodOutput output) {
         this.getValue(output);
         output.f2l();
         output.returnType(Type.LONG_TYPE);
      }

      public void defineAsFloat(MethodOutput output) {
         this.getValue(output);
         output.returnType(Type.FLOAT_TYPE);
      }

      public void defineAsDouble(MethodOutput output) {
         this.getValue(output);
         output.f2d();
         output.returnType(Type.DOUBLE_TYPE);
      }

      public void defineAsString(MethodOutput output) {
         this.getValue(output);
         output.invokeStatic(Float.class, "toString", String.class, Float.TYPE);
         output.returnObject();
      }

      public void defineAs(MethodOutput output) {
         int localValue = output.local(Type.FLOAT_TYPE);
         this.getValue(output);
         output.store(Type.FLOAT_TYPE, localValue);
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeFloat.this.getName());
         if (expansion != null) {
            expansion.compileAnyCast(ZenType.FLOAT, output, this.environment, localValue, 1);
         }

         AnyClassWriter.throwCastException(output, "float", 1);
      }

      public void defineIs(MethodOutput output) {
         Label lblEq = new Label();
         output.loadObject(1);
         output.constant(Type.FLOAT_TYPE);
         output.ifACmpEq(lblEq);
         output.iConst0();
         output.returnInt();
         output.label(lblEq);
         output.iConst1();
         output.returnInt();
      }

      public void defineGetNumberType(MethodOutput output) {
         output.constant(Integer.valueOf(5));
         output.returnInt();
      }

      public void defineIteratorSingle(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "float", "iterator");
      }

      public void defineIteratorMulti(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "float", "iterator");
      }

      private void getValue(MethodOutput output) {
         output.loadObject(0);
         output.getField("any/AnyFloat", "value", "F");
      }

      public void defineEquals(MethodOutput output) {
         Label lblNope = new Label();
         output.loadObject(1);
         output.instanceOf("stanhebben/zenscript/value/IAny");
         output.ifEQ(lblNope);
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASFLOAT.invokeVirtual(output);
         output.fCmp();
         output.ifICmpNE(lblNope);
         output.iConst1();
         output.returnInt();
         output.label(lblNope);
         output.iConst0();
         output.returnInt();
      }

      public void defineHashCode(MethodOutput output) {
         this.getValue(output);
         output.invokeStatic(Float.class, "floatToIntBits", Float.TYPE, Integer.TYPE);
         output.returnInt();
      }
   }
}
