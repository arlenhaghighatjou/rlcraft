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
import stanhebben.zenscript.type.casting.CastingRuleI2D;
import stanhebben.zenscript.type.casting.CastingRuleI2F;
import stanhebben.zenscript.type.casting.CastingRuleI2L;
import stanhebben.zenscript.type.casting.CastingRuleNone;
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

public class ZenTypeByte extends ZenType {
   public static final ZenTypeByte INSTANCE = new ZenTypeByte();
   private static final String ANY_NAME = "any/AnyByte";
   private static final String ANY_NAME_2 = "any.AnyByte";

   private ZenTypeByte() {
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return null;
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      rules.registerCastingRule(BYTEOBJECT, new CastingRuleStaticMethod(BYTE_VALUEOF));
      rules.registerCastingRule(SHORT, new CastingRuleNone(BYTE, SHORT));
      rules.registerCastingRule(SHORTOBJECT, new CastingRuleStaticMethod(SHORT_VALUEOF));
      rules.registerCastingRule(INT, new CastingRuleNone(BYTE, INT));
      rules.registerCastingRule(INTOBJECT, new CastingRuleStaticMethod(INT_VALUEOF));
      rules.registerCastingRule(LONG, new CastingRuleI2L((ICastingRule)null));
      rules.registerCastingRule(LONGOBJECT, new CastingRuleStaticMethod(LONG_VALUEOF, new CastingRuleI2L((ICastingRule)null)));
      rules.registerCastingRule(FLOAT, new CastingRuleI2F((ICastingRule)null));
      rules.registerCastingRule(FLOATOBJECT, new CastingRuleStaticMethod(FLOAT_VALUEOF, new CastingRuleI2F((ICastingRule)null)));
      rules.registerCastingRule(DOUBLE, new CastingRuleI2D((ICastingRule)null));
      rules.registerCastingRule(DOUBLEOBJECT, new CastingRuleStaticMethod(DOUBLE_VALUEOF, new CastingRuleI2D((ICastingRule)null)));
      rules.registerCastingRule(STRING, new CastingRuleStaticMethod(BYTE_TOSTRING_STATIC));
      rules.registerCastingRule(ANY, new CastingRuleStaticMethod(JavaMethod.getStatic(this.getAnyClassName(environment), "valueOf", ANY, BYTE)));
      if (followCasters) {
         this.constructExpansionCastingRules(environment, rules);
      }

   }

   public Class toJavaClass() {
      return Byte.TYPE;
   }

   public Type toASMType() {
      return Type.BYTE_TYPE;
   }

   public int getNumberType() {
      return 1;
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      IPartialExpression result = this.memberExpansion(position, environment, value.eval(environment), name);
      if (result == null) {
         environment.error(position, "bool value has no member named " + name);
         return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
      } else {
         return result;
      }
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return null;
   }

   public String getSignature() {
      return "B";
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
      environment.error(position, "byte doesn't support this operation");
      return new ExpressionInvalid(position);
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      return new ExpressionArithmeticCompare(position, type, left, right.cast(position, environment, this));
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      environment.error(position, "cannot call a byte value");
      return new ExpressionInvalid(position, INSTANCE);
   }

   public String getName() {
      return "byte";
   }

   public String getAnyClassName(IEnvironmentGlobal environment) {
      if (!environment.containsClass("any.AnyByte")) {
         environment.putClass("any.AnyByte", new byte[0]);
         environment.putClass("any.AnyByte", AnyClassWriter.construct(new ZenTypeByte.AnyDefinitionByte(environment), "any/AnyByte", Type.BYTE_TYPE));
      }

      return "any/AnyByte";
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionInt(position, 0L, ZenType.BYTE);
   }

   private class AnyDefinitionByte implements IAnyDefinition {
      private final IEnvironmentGlobal environment;

      public AnyDefinitionByte(IEnvironmentGlobal environment) {
         this.environment = environment;
      }

      public void defineMembers(ClassVisitor output) {
         output.visitField(2, "value", "B", (String)null, (Object)null);
         MethodOutput valueOf = new MethodOutput(output, 9, "valueOf", "(B)" + ZenTypeUtil.signature(IAny.class), (String)null, (String[])null);
         valueOf.start();
         valueOf.newObject("any/AnyByte");
         valueOf.dup();
         valueOf.load(Type.BYTE_TYPE, 0);
         valueOf.construct("any/AnyByte", "B");
         valueOf.returnObject();
         valueOf.end();
         MethodOutput constructor = new MethodOutput(output, 1, "<init>", "(B)V", (String)null, (String[])null);
         constructor.start();
         constructor.loadObject(0);
         constructor.invokeSpecial(ZenTypeUtil.internal(Object.class), "<init>", "()V");
         constructor.loadObject(0);
         constructor.load(Type.BYTE_TYPE, 1);
         constructor.putField("any/AnyByte", "value", "B");
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
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeByte.this.getName());
         if (expansion != null) {
            expansion.compileAnyCanCastImplicit(ZenType.BYTE, output, this.environment, 0);
         }

         output.iConst0();
         output.returnInt();
         output.label(lblCan);
         output.iConst1();
         output.returnInt();
      }

      public void defineStaticAs(MethodOutput output) {
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeByte.this.getName());
         if (expansion != null) {
            expansion.compileAnyCast(ZenType.BYTE, output, this.environment, 0, 1);
         }

         AnyClassWriter.throwCastException(output, "byte", 1);
      }

      public void defineNot(MethodOutput output) {
         output.newObject("any/AnyByte");
         output.dup();
         this.getValue(output);
         output.iNot();
         output.invokeSpecial("any/AnyByte", "<init>", "(B)V");
         output.returnObject();
      }

      public void defineNeg(MethodOutput output) {
         output.newObject("any/AnyByte");
         output.dup();
         this.getValue(output);
         output.iNeg();
         output.invokeSpecial("any/AnyByte", "<init>", "(B)V");
         output.returnObject();
      }

      public void defineAdd(MethodOutput output) {
         output.newObject("any/AnyByte");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBYTE.invokeVirtual(output);
         output.iAdd();
         output.invokeSpecial("any/AnyByte", "<init>", "(B)V");
         output.returnObject();
      }

      public void defineSub(MethodOutput output) {
         output.newObject("any/AnyByte");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBYTE.invokeVirtual(output);
         output.iSub();
         output.invokeSpecial("any/AnyByte", "<init>", "(B)V");
         output.returnObject();
      }

      public void defineCat(MethodOutput output) {
         output.newObject(StringBuilder.class);
         output.dup();
         output.invokeSpecial(ZenTypeUtil.internal(StringBuilder.class), "<init>", "()V");
         this.getValue(output);
         output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, Integer.TYPE);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASSTRING.invokeVirtual(output);
         output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, String.class);
         output.invokeVirtual(StringBuilder.class, "toString", String.class);
         output.invokeStatic(ZenType.STRING.getAnyClassName(this.environment), "valueOf", "(Ljava/lang/String;)" + ZenTypeUtil.signature(IAny.class));
         output.returnObject();
      }

      public void defineMul(MethodOutput output) {
         output.newObject("any/AnyByte");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBYTE.invokeVirtual(output);
         output.iMul();
         output.invokeSpecial("any/AnyByte", "<init>", "(B)V");
         output.returnObject();
      }

      public void defineDiv(MethodOutput output) {
         output.newObject("any/AnyByte");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBYTE.invokeVirtual(output);
         output.iDiv();
         output.invokeSpecial("any/AnyByte", "<init>", "(B)V");
         output.returnObject();
      }

      public void defineMod(MethodOutput output) {
         output.newObject("any/AnyByte");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBYTE.invokeVirtual(output);
         output.iRem();
         output.invokeSpecial("any/AnyByte", "<init>", "(B)V");
         output.returnObject();
      }

      public void defineAnd(MethodOutput output) {
         output.newObject("any/AnyByte");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBYTE.invokeVirtual(output);
         output.iAnd();
         output.invokeSpecial("any/AnyByte", "<init>", "(B)V");
         output.returnObject();
      }

      public void defineOr(MethodOutput output) {
         output.newObject("any/AnyByte");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBYTE.invokeVirtual(output);
         output.iOr();
         output.invokeSpecial("any/AnyByte", "<init>", "(B)V");
         output.returnObject();
      }

      public void defineXor(MethodOutput output) {
         output.newObject("any/AnyByte");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBYTE.invokeVirtual(output);
         output.iXor();
         output.invokeSpecial("any/AnyByte", "<init>", "(B)V");
         output.returnObject();
      }

      public void defineRange(MethodOutput output) {
         output.aConstNull();
         output.returnObject();
      }

      public void defineCompareTo(MethodOutput output) {
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBYTE.invokeVirtual(output);
         output.iSub();
         output.returnInt();
      }

      public void defineContains(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "byte", "in");
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
         AnyClassWriter.throwUnsupportedException(output, "byte", "get []");
      }

      public void defineIndexSet(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "byte", "set []");
      }

      public void defineCall(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "byte", "call");
      }

      public void defineAsBool(MethodOutput output) {
         AnyClassWriter.throwCastException(output, "any/AnyByte", "bool");
      }

      public void defineAsByte(MethodOutput output) {
         this.getValue(output);
         output.returnType(Type.BYTE_TYPE);
      }

      public void defineAsShort(MethodOutput output) {
         this.getValue(output);
         output.returnType(Type.SHORT_TYPE);
      }

      public void defineAsInt(MethodOutput output) {
         this.getValue(output);
         output.returnType(Type.INT_TYPE);
      }

      public void defineAsLong(MethodOutput output) {
         this.getValue(output);
         output.i2l();
         output.returnType(Type.LONG_TYPE);
      }

      public void defineAsFloat(MethodOutput output) {
         this.getValue(output);
         output.i2f();
         output.returnType(Type.FLOAT_TYPE);
      }

      public void defineAsDouble(MethodOutput output) {
         this.getValue(output);
         output.i2d();
         output.returnType(Type.DOUBLE_TYPE);
      }

      public void defineAsString(MethodOutput output) {
         this.getValue(output);
         output.invokeStatic(Byte.class, "toString", String.class, Byte.TYPE);
         output.returnObject();
      }

      public void defineAs(MethodOutput output) {
         int localValue = output.local(Type.BYTE_TYPE);
         this.getValue(output);
         output.store(Type.BYTE_TYPE, localValue);
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeByte.this.getName());
         if (expansion != null) {
            expansion.compileAnyCast(ZenType.BYTE, output, this.environment, localValue, 1);
         }

         AnyClassWriter.throwCastException(output, "byte", 1);
      }

      public void defineIs(MethodOutput output) {
         Label lblEq = new Label();
         output.loadObject(1);
         output.constant(Type.BYTE_TYPE);
         output.ifACmpEq(lblEq);
         output.iConst0();
         output.returnInt();
         output.label(lblEq);
         output.iConst1();
         output.returnInt();
      }

      public void defineGetNumberType(MethodOutput output) {
         output.constant(Integer.valueOf(1));
         output.returnInt();
      }

      public void defineIteratorSingle(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "byte", "iterator");
      }

      public void defineIteratorMulti(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "byte", "iterator");
      }

      public void defineEquals(MethodOutput output) {
         Label lblNope = new Label();
         output.loadObject(1);
         output.instanceOf("stanhebben/zenscript/value/IAny");
         output.ifEQ(lblNope);
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBYTE.invokeVirtual(output);
         output.ifICmpNE(lblNope);
         output.iConst1();
         output.returnInt();
         output.label(lblNope);
         output.iConst0();
         output.returnInt();
      }

      public void defineHashCode(MethodOutput output) {
         this.getValue(output);
         output.returnInt();
      }

      private void getValue(MethodOutput output) {
         output.loadObject(0);
         output.getField("any/AnyByte", "value", "B");
      }
   }
}
