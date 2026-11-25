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
import stanhebben.zenscript.expression.ExpressionBool;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.casting.CastingRuleStaticMethod;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.AnyClassWriter;
import stanhebben.zenscript.util.IAnyDefinition;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;
import stanhebben.zenscript.value.IAny;

public class ZenTypeBool extends ZenType {
   private static final String ANY_NAME = "any/AnyBool";
   private static final String ANY_NAME_2 = "any.AnyBool";
   private static final String ANY_NAME_DESC = "Lany/AnyBool;";

   public ZenTypeBool() {
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return null;
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      rules.registerCastingRule(STRING, new CastingRuleStaticMethod(BOOL_TOSTRING_STATIC));
      rules.registerCastingRule(BOOLOBJECT, new CastingRuleStaticMethod(BOOL_VALUEOF));
      rules.registerCastingRule(ANY, new CastingRuleStaticMethod(JavaMethod.getStatic(this.getAnyClassName(environment), "valueOf", ANY, BOOL)));
      if (followCasters) {
         this.constructExpansionCastingRules(environment, rules);
      }

   }

   public boolean canCastExplicit(ZenType type, IEnvironmentGlobal environment) {
      return this.canCastImplicit(type, environment);
   }

   public Class toJavaClass() {
      return Boolean.TYPE;
   }

   public Type toASMType() {
      return Type.BOOLEAN_TYPE;
   }

   public int getNumberType() {
      return 0;
   }

   public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      return new ExpressionArithmeticUnary(position, operator, value);
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      if (operator == OperatorType.CAT) {
         return STRING.binary(position, environment, left.cast(position, environment, STRING), right.cast(position, environment, STRING), OperatorType.CAT);
      } else if (right.getType().canCastImplicit(BOOL, environment)) {
         switch(operator) {
         case AND:
         case OR:
         case XOR:
            if (right.getType() != BOOL) {
               right = right.cast(position, environment, BOOL);
            }

            return new ExpressionArithmeticBinary(position, operator, left, right);
         default:
            environment.error(position, "unsupported bool operator: " + operator);
            return new ExpressionInvalid(position, BOOL);
         }
      } else {
         environment.error(right.getPosition(), "not a valid bool value");
         return new ExpressionInvalid(position, BOOL);
      }
   }

   public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      environment.error(position, "operation not supported on a bool value");
      return new ExpressionInvalid(position, BOOL);
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      if (type != CompareType.EQ && type != CompareType.NE) {
         environment.error(position, "such comparison not supported on a bool");
         return new ExpressionInvalid(position, BOOL);
      } else {
         return new ExpressionArithmeticCompare(position, type, left, right);
      }
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      environment.error(position, "cannot call a boolean value");
      return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      IPartialExpression result = this.memberExpansion(position, environment, value.eval(environment), name);
      if (result == null) {
         environment.error(position, "bool value has no members");
         return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
      } else {
         return result;
      }
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      environment.error(position, "bool type has no static members");
      return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
   }

   public String getSignature() {
      return "Z";
   }

   public boolean isPointer() {
      return false;
   }

   public String getName() {
      return "bool";
   }

   public String getAnyClassName(IEnvironmentGlobal environment) {
      if (!environment.containsClass("any.AnyBool")) {
         environment.putClass("any.AnyBool", new byte[0]);
         environment.putClass("any.AnyBool", AnyClassWriter.construct(new ZenTypeBool.AnyDefinitionBool(environment), "any/AnyBool", Type.BOOLEAN_TYPE));
      }

      return "any/AnyBool";
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionBool(position, false);
   }

   private class AnyDefinitionBool implements IAnyDefinition {
      private final IEnvironmentGlobal environment;

      public AnyDefinitionBool(IEnvironmentGlobal environment) {
         this.environment = environment;
      }

      public void defineMembers(ClassVisitor output) {
         output.visitField(25, "TRUE", "Lany/AnyBool;", (String)null, (Object)null);
         output.visitField(25, "FALSE", "Lany/AnyBool;", (String)null, (Object)null);
         output.visitField(2, "value", "Z", (String)null, (Object)null);
         MethodOutput clinit = new MethodOutput(output, 9, "<clinit>", "()V", (String)null, (String[])null);
         clinit.start();
         clinit.newObject("any/AnyBool");
         clinit.dup();
         clinit.iConst0();
         clinit.invokeSpecial("any/AnyBool", "<init>", "(Z)V");
         clinit.putStaticField("any/AnyBool", "FALSE", "Lany/AnyBool;");
         clinit.newObject("any/AnyBool");
         clinit.dup();
         clinit.iConst1();
         clinit.invokeSpecial("any/AnyBool", "<init>", "(Z)V");
         clinit.putStaticField("any/AnyBool", "TRUE", "Lany/AnyBool;");
         clinit.returnType(Type.VOID_TYPE);
         clinit.end();
         MethodOutput valueOf = new MethodOutput(output, 9, "valueOf", "(Z)" + ZenTypeUtil.signature(IAny.class), (String)null, (String[])null);
         Label lblFalse = new Label();
         valueOf.start();
         valueOf.load(Type.BOOLEAN_TYPE, 0);
         valueOf.ifEQ(lblFalse);
         valueOf.getStaticField("any/AnyBool", "TRUE", "Lany/AnyBool;");
         valueOf.returnObject();
         valueOf.label(lblFalse);
         valueOf.getStaticField("any/AnyBool", "FALSE", "Lany/AnyBool;");
         valueOf.returnObject();
         valueOf.end();
         MethodOutput constructor = new MethodOutput(output, 1, "<init>", "(Z)V", (String)null, (String[])null);
         constructor.start();
         constructor.loadObject(0);
         constructor.invokeSpecial(ZenTypeUtil.internal(Object.class), "<init>", "()V");
         constructor.loadObject(0);
         constructor.load(Type.BOOLEAN_TYPE, 1);
         constructor.putField("any/AnyBool", "value", "Z");
         constructor.returnType(Type.VOID_TYPE);
         constructor.end();
      }

      public void defineStaticCanCastImplicit(MethodOutput output) {
         Label lblCan = new Label();
         output.constant(Type.BOOLEAN_TYPE);
         output.loadObject(0);
         output.ifACmpEq(lblCan);
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeBool.this.getName());
         if (expansion != null) {
            expansion.compileAnyCanCastImplicit(ZenType.BOOL, output, this.environment, 0);
         }

         output.iConst0();
         output.returnInt();
         output.label(lblCan);
         output.iConst1();
         output.returnInt();
      }

      public void defineStaticAs(MethodOutput output) {
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeBool.this.getName());
         if (expansion != null) {
            expansion.compileAnyCast(ZenType.BOOL, output, this.environment, 0, 1);
         }

         AnyClassWriter.throwCastException(output, "bool", 1);
      }

      public void defineNot(MethodOutput output) {
         this.getValue(output);
         output.iNot();
         this.valueOf(output);
         output.returnObject();
      }

      public void defineNeg(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "bool", "negate");
      }

      public void defineAdd(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "bool", "+");
      }

      public void defineSub(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "bool", "-");
      }

      public void defineCat(MethodOutput output) {
         output.newObject(StringBuilder.class);
         output.dup();
         output.invokeSpecial(ZenTypeUtil.internal(StringBuilder.class), "<init>", "()V");
         this.getValue(output);
         output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, Boolean.TYPE);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASSTRING.invokeVirtual(output);
         output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, String.class);
         output.invokeVirtual(StringBuilder.class, "toString", String.class);
         output.invokeStatic(ZenType.STRING.getAnyClassName(this.environment), "valueOf", "(Ljava/lang/String;)" + ZenTypeUtil.signature(IAny.class));
         output.returnObject();
      }

      public void defineMul(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "bool", "*");
      }

      public void defineDiv(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "bool", "/");
      }

      public void defineMod(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "bool", "%");
      }

      public void defineAnd(MethodOutput output) {
         output.newObject("any/AnyBool");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBOOL.invokeVirtual(output);
         output.iAnd();
         this.valueOf(output);
         output.returnObject();
      }

      public void defineOr(MethodOutput output) {
         output.newObject("any/AnyBool");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBOOL.invokeVirtual(output);
         output.iOr();
         this.valueOf(output);
         output.returnObject();
      }

      public void defineXor(MethodOutput output) {
         output.newObject("any/AnyBool");
         output.dup();
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBYTE.invokeVirtual(output);
         output.iXor();
         this.valueOf(output);
         output.returnObject();
      }

      public void defineRange(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "bool", "range");
      }

      public void defineCompareTo(MethodOutput output) {
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASBOOL.invokeVirtual(output);
         output.iSub();
         output.returnInt();
      }

      public void defineContains(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "bool", "in");
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
         AnyClassWriter.throwUnsupportedException(output, "bool", "get []");
      }

      public void defineIndexSet(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "bool", "set []");
      }

      public void defineCall(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "bool", "call");
      }

      public void defineAsBool(MethodOutput output) {
         AnyClassWriter.throwCastException(output, "any/AnyBool", "bool");
      }

      public void defineAsByte(MethodOutput output) {
         AnyClassWriter.throwCastException(output, "bool", "byte");
      }

      public void defineAsShort(MethodOutput output) {
         AnyClassWriter.throwCastException(output, "bool", "short");
      }

      public void defineAsInt(MethodOutput output) {
         AnyClassWriter.throwCastException(output, "bool", "int");
      }

      public void defineAsLong(MethodOutput output) {
         AnyClassWriter.throwCastException(output, "bool", "long");
      }

      public void defineAsFloat(MethodOutput output) {
         AnyClassWriter.throwCastException(output, "bool", "float");
      }

      public void defineAsDouble(MethodOutput output) {
         AnyClassWriter.throwCastException(output, "bool", "double");
      }

      public void defineAsString(MethodOutput output) {
         this.getValue(output);
         output.invokeStatic(Boolean.class, "toString", String.class, Boolean.TYPE);
         output.returnObject();
      }

      public void defineAs(MethodOutput output) {
         int localValue = output.local(Type.BYTE_TYPE);
         this.getValue(output);
         output.store(Type.BYTE_TYPE, localValue);
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeBool.this.getName());
         if (expansion != null) {
            expansion.compileAnyCast(ZenType.BYTE, output, this.environment, localValue, 1);
         }

         AnyClassWriter.throwCastException(output, "bool", 1);
      }

      public void defineIs(MethodOutput output) {
         Label lblEq = new Label();
         output.loadObject(1);
         output.constant(Type.BOOLEAN_TYPE);
         output.ifACmpEq(lblEq);
         output.iConst0();
         output.returnInt();
         output.label(lblEq);
         output.iConst1();
         output.returnInt();
      }

      public void defineGetNumberType(MethodOutput output) {
         output.iConst0();
         output.returnInt();
      }

      public void defineIteratorSingle(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "bool", "iterator");
      }

      public void defineIteratorMulti(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "bool", "iterator");
      }

      public void defineEquals(MethodOutput output) {
         Label lblEqual = new Label();
         output.loadObject(0);
         output.loadObject(1);
         output.ifACmpEq(lblEqual);
         output.iConst0();
         output.returnInt();
         output.label(lblEqual);
         output.iConst1();
         output.returnInt();
      }

      public void defineHashCode(MethodOutput output) {
         output.invokeSpecial("java/lang/Object", "hashCode", "()I");
      }

      private void getValue(MethodOutput output) {
         output.loadObject(0);
         output.getField("any/AnyBool", "value", "Z");
      }

      private void valueOf(MethodOutput output) {
         output.invokeStatic("any/AnyBool", "valueOf", "(Z)" + ZenTypeUtil.signature(IAny.class));
      }
   }
}
