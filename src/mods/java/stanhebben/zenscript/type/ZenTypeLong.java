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
import stanhebben.zenscript.expression.ExpressionInt;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.casting.CastingRuleI2B;
import stanhebben.zenscript.type.casting.CastingRuleI2S;
import stanhebben.zenscript.type.casting.CastingRuleL2D;
import stanhebben.zenscript.type.casting.CastingRuleL2F;
import stanhebben.zenscript.type.casting.CastingRuleL2I;
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

public class ZenTypeLong extends ZenType {
   public static final ZenTypeLong INSTANCE = new ZenTypeLong();
   private static final String ANY_NAME = "any/AnyLong";
   private static final String ANY_NAME_2 = "any.AnyLong";

   private ZenTypeLong() {
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return null;
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      rules.registerCastingRule(BYTE, new CastingRuleI2B(new CastingRuleL2I((ICastingRule)null)));
      rules.registerCastingRule(BYTEOBJECT, new CastingRuleStaticMethod(BYTE_VALUEOF, new CastingRuleI2B(new CastingRuleL2I((ICastingRule)null))));
      rules.registerCastingRule(SHORT, new CastingRuleI2S(new CastingRuleL2I((ICastingRule)null)));
      rules.registerCastingRule(SHORTOBJECT, new CastingRuleStaticMethod(SHORT_VALUEOF, new CastingRuleI2S(new CastingRuleL2I((ICastingRule)null))));
      rules.registerCastingRule(INT, new CastingRuleL2I((ICastingRule)null));
      rules.registerCastingRule(INTOBJECT, new CastingRuleStaticMethod(INT_VALUEOF, new CastingRuleL2I((ICastingRule)null)));
      rules.registerCastingRule(LONGOBJECT, new CastingRuleStaticMethod(LONG_VALUEOF));
      rules.registerCastingRule(FLOAT, new CastingRuleL2F((ICastingRule)null));
      rules.registerCastingRule(FLOATOBJECT, new CastingRuleStaticMethod(FLOAT_VALUEOF, new CastingRuleL2F((ICastingRule)null)));
      rules.registerCastingRule(DOUBLE, new CastingRuleL2D((ICastingRule)null));
      rules.registerCastingRule(DOUBLEOBJECT, new CastingRuleStaticMethod(DOUBLE_VALUEOF, new CastingRuleL2D((ICastingRule)null)));
      rules.registerCastingRule(STRING, new CastingRuleStaticMethod(LONG_TOSTRING_STATIC));
      rules.registerCastingRule(ANY, new CastingRuleStaticMethod(JavaMethod.getStatic(this.getAnyClassName(environment), "valueOf", ANY, LONG)));
      if (followCasters) {
         this.constructExpansionCastingRules(environment, rules);
      }

   }

   public Type toASMType() {
      return Type.LONG_TYPE;
   }

   public int getNumberType() {
      return 4;
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      IPartialExpression result = this.memberExpansion(position, environment, value.eval(environment), name);
      if (result == null) {
         environment.error(position, "long value has no members");
         return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
      } else {
         return result;
      }
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return null;
   }

   public String getSignature() {
      return "J";
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
      environment.error(position, "long doesn't support this operation");
      return new ExpressionInvalid(position);
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      return new ExpressionArithmeticCompare(position, type, left, right.cast(position, environment, this));
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      environment.error(position, "cannot call a long value");
      return new ExpressionInvalid(position);
   }

   public Class toJavaClass() {
      return Long.TYPE;
   }

   public String getName() {
      return "long";
   }

   public String getAnyClassName(IEnvironmentGlobal environment) {
      if (!environment.containsClass("any.AnyLong")) {
         environment.putClass("any.AnyLong", new byte[0]);
         environment.putClass("any.AnyLong", AnyClassWriter.construct(new ZenTypeLong.AnyDefinitionLong(environment), "any/AnyLong", Type.LONG_TYPE));
      }

      return "any/AnyLong";
   }

   public boolean isLarge() {
      return true;
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionInt(position, 0L, LONG);
   }

   private class AnyDefinitionLong implements IAnyDefinition {
      private final IEnvironmentGlobal environment;

      public AnyDefinitionLong(IEnvironmentGlobal environment) {
         this.environment = environment;
      }

      public void defineMembers(ClassVisitor output) {
         output.visitField(2, "value", "J", (String)null, (Object)null);
         MethodOutput valueOf = new MethodOutput(output, 9, "valueOf", "(J)" + ZenTypeUtil.signature(IAny.class), (String)null, (String[])null);
         valueOf.start();
         valueOf.newObject("any/AnyLong");
         valueOf.dup();
         valueOf.load(Type.LONG_TYPE, 0);
         valueOf.construct("any/AnyLong", "J");
         valueOf.returnObject();
         valueOf.end();
         MethodOutput constructor = new MethodOutput(output, 1, "<init>", "(J)V", (String)null, (String[])null);
         constructor.start();
         constructor.loadObject(0);
         constructor.invokeSpecial(ZenTypeUtil.internal(Object.class), "<init>", "()V");
         constructor.loadObject(0);
         constructor.load(Type.LONG_TYPE, 1);
         constructor.putField("any/AnyLong", "value", "J");
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
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeLong.this.getName());
         if (expansion != null) {
            expansion.compileAnyCanCastImplicit(ZenType.LONG, output, this.environment, 0);
         }

         output.iConst0();
         output.returnInt();
         output.label(lblCan);
         output.iConst1();
         output.returnInt();
      }

      public void defineStaticAs(MethodOutput output) {
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeLong.this.getName());
         if (expansion != null) {
            expansion.compileAnyCast(ZenType.LONG, output, this.environment, 0, 1);
         }

         AnyClassWriter.throwCastException(output, "long", 1);
      }

      public void defineNot(MethodOutput output) {
         output.newObject("any/AnyLong");
         output.dup();
         this.getValue(output);
         output.lNot();
         output.invokeSpecial("any/AnyLong", "<init>", "(J)V");
         output.returnObject();
      }

      public void defineNeg(MethodOutput output) {
         output.newObject("any/AnyLong");
         output.dup();
         this.getValue(output);
         output.lNeg();
         output.invokeSpecial("any/AnyLong", "<init>", "(J)V");
         output.returnObject();
      }

      public void defineAdd(MethodOutput output) {
         output.newObject("any/AnyLong");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASLONG.invokeVirtual(output);
         output.lAdd();
         output.invokeSpecial("any/AnyLong", "<init>", "(J)V");
         output.returnObject();
      }

      public void defineSub(MethodOutput output) {
         output.newObject("any/AnyLong");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASLONG.invokeVirtual(output);
         output.lSub();
         output.invokeSpecial("any/AnyLong", "<init>", "(J)V");
         output.returnObject();
      }

      public void defineCat(MethodOutput output) {
         output.newObject(StringBuilder.class);
         output.dup();
         output.invokeSpecial(ZenTypeUtil.internal(StringBuilder.class), "<init>", "()V");
         this.getValue(output);
         output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, Long.TYPE);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASSTRING.invokeVirtual(output);
         output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, String.class);
         output.invokeVirtual(StringBuilder.class, "toString", String.class);
         output.invokeStatic(ZenType.STRING.getAnyClassName(this.environment), "valueOf", "(Ljava/lang/String;)" + ZenTypeUtil.signature(IAny.class));
         output.returnObject();
      }

      public void defineMul(MethodOutput output) {
         output.newObject("any/AnyLong");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASLONG.invokeVirtual(output);
         output.lMul();
         output.invokeSpecial("any/AnyLong", "<init>", "(J)V");
         output.returnObject();
      }

      public void defineDiv(MethodOutput output) {
         output.newObject("any/AnyLong");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASLONG.invokeVirtual(output);
         output.lDiv();
         output.invokeSpecial("any/AnyLong", "<init>", "(J)V");
         output.returnObject();
      }

      public void defineMod(MethodOutput output) {
         output.newObject("any/AnyLong");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASLONG.invokeVirtual(output);
         output.lRem();
         output.invokeSpecial("any/AnyLong", "<init>", "(J)V");
         output.returnObject();
      }

      public void defineAnd(MethodOutput output) {
         output.newObject("any/AnyLong");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASLONG.invokeVirtual(output);
         output.lAnd();
         output.invokeSpecial("any/AnyLong", "<init>", "(J)V");
         output.returnObject();
      }

      public void defineOr(MethodOutput output) {
         output.newObject("any/AnyLong");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASLONG.invokeVirtual(output);
         output.lOr();
         output.invokeSpecial("any/AnyLong", "<init>", "(J)V");
         output.returnObject();
      }

      public void defineXor(MethodOutput output) {
         output.newObject("any/AnyLong");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASLONG.invokeVirtual(output);
         output.lXor();
         output.invokeSpecial("any/AnyLong", "<init>", "(J)V");
         output.returnObject();
      }

      public void defineRange(MethodOutput output) {
         output.aConstNull();
         output.returnObject();
      }

      public void defineCompareTo(MethodOutput output) {
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASLONG.invokeVirtual(output);
         output.invokeStatic(Long.class, "compare", Integer.TYPE, Long.TYPE, Long.TYPE);
         output.returnInt();
      }

      public void defineContains(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "long", "in");
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
         AnyClassWriter.throwUnsupportedException(output, "long", "get []");
      }

      public void defineIndexSet(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "long", "set []");
      }

      public void defineCall(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "long", "call");
      }

      public void defineAsBool(MethodOutput output) {
         AnyClassWriter.throwCastException(output, "any/AnyLong", "bool");
      }

      public void defineAsByte(MethodOutput output) {
         this.getValue(output);
         output.l2i();
         output.i2b();
         output.returnType(Type.BYTE_TYPE);
      }

      public void defineAsShort(MethodOutput output) {
         this.getValue(output);
         output.l2i();
         output.i2s();
         output.returnType(Type.SHORT_TYPE);
      }

      public void defineAsInt(MethodOutput output) {
         this.getValue(output);
         output.l2i();
         output.returnType(Type.INT_TYPE);
      }

      public void defineAsLong(MethodOutput output) {
         this.getValue(output);
         output.returnType(Type.LONG_TYPE);
      }

      public void defineAsFloat(MethodOutput output) {
         this.getValue(output);
         output.l2f();
         output.returnType(Type.FLOAT_TYPE);
      }

      public void defineAsDouble(MethodOutput output) {
         this.getValue(output);
         output.l2d();
         output.returnType(Type.DOUBLE_TYPE);
      }

      public void defineAsString(MethodOutput output) {
         this.getValue(output);
         output.invokeStatic(Long.class, "toString", String.class, Long.TYPE);
         output.returnObject();
      }

      public void defineAs(MethodOutput output) {
         int localValue = output.local(Type.LONG_TYPE);
         this.getValue(output);
         output.store(Type.LONG_TYPE, localValue);
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeLong.this.getName());
         if (expansion != null) {
            expansion.compileAnyCast(ZenType.LONG, output, this.environment, localValue, 1);
         }

         AnyClassWriter.throwCastException(output, "long", 1);
      }

      public void defineIs(MethodOutput output) {
         Label lblEq = new Label();
         output.loadObject(1);
         output.constant(Type.LONG_TYPE);
         output.ifACmpEq(lblEq);
         output.iConst0();
         output.returnInt();
         output.label(lblEq);
         output.iConst1();
         output.returnInt();
      }

      public void defineGetNumberType(MethodOutput output) {
         output.constant(Integer.valueOf(4));
         output.returnInt();
      }

      public void defineIteratorSingle(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "long", "iterator");
      }

      public void defineIteratorMulti(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "long", "iterator");
      }

      private void getValue(MethodOutput output) {
         output.loadObject(0);
         output.getField("any/AnyLong", "value", "J");
      }

      public void defineEquals(MethodOutput output) {
         Label lblNope = new Label();
         output.loadObject(0);
         output.instanceOf("stanhebben/zenscript/value/IAny");
         output.ifEQ(lblNope);
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASLONG.invokeVirtual(output);
         output.lCmp();
         output.ifICmpNE(lblNope);
         output.iConst1();
         output.returnInt();
         output.label(lblNope);
         output.iConst0();
         output.returnInt();
      }

      public void defineHashCode(MethodOutput output) {
         this.getValue(output);
         output.l2i();
         this.getValue(output);
         output.constant(Integer.valueOf(32));
         output.lShr();
         output.l2i();
         output.iXor();
         output.returnInt();
      }
   }
}
