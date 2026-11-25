package stanhebben.zenscript.type;

import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import stanhebben.zenscript.TypeExpansion;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallVirtual;
import stanhebben.zenscript.expression.ExpressionCompareGeneric;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionNull;
import stanhebben.zenscript.expression.ExpressionStringConcat;
import stanhebben.zenscript.expression.ExpressionStringContains;
import stanhebben.zenscript.expression.ExpressionStringIndex;
import stanhebben.zenscript.expression.ExpressionStringMethod;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.casting.CastingRuleNullableStaticMethod;
import stanhebben.zenscript.type.casting.CastingRuleStaticMethod;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.AnyClassWriter;
import stanhebben.zenscript.util.IAnyDefinition;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;
import stanhebben.zenscript.value.IAny;

public class ZenTypeString extends ZenType {
   public static final ZenTypeString INSTANCE = new ZenTypeString();
   private static final String ANY_NAME = "any/AnyString";
   private static final String ANY_NAME_2 = "any.AnyString";
   private final Type type = Type.getType(String.class);

   private ZenTypeString() {
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return null;
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      rules.registerCastingRule(BOOL, new CastingRuleStaticMethod(PARSE_BOOL));
      rules.registerCastingRule(BOOLOBJECT, new CastingRuleNullableStaticMethod(PARSE_BOOL_OBJECT));
      rules.registerCastingRule(BYTE, new CastingRuleStaticMethod(PARSE_BYTE));
      rules.registerCastingRule(BYTEOBJECT, new CastingRuleNullableStaticMethod(PARSE_BYTE_OBJECT));
      rules.registerCastingRule(SHORT, new CastingRuleStaticMethod(PARSE_SHORT));
      rules.registerCastingRule(SHORTOBJECT, new CastingRuleNullableStaticMethod(PARSE_SHORT_OBJECT));
      rules.registerCastingRule(INT, new CastingRuleStaticMethod(PARSE_INT));
      rules.registerCastingRule(INTOBJECT, new CastingRuleNullableStaticMethod(PARSE_INT_OBJECT));
      rules.registerCastingRule(LONG, new CastingRuleStaticMethod(PARSE_LONG));
      rules.registerCastingRule(LONGOBJECT, new CastingRuleNullableStaticMethod(PARSE_LONG_OBJECT));
      rules.registerCastingRule(FLOAT, new CastingRuleStaticMethod(PARSE_FLOAT));
      rules.registerCastingRule(FLOATOBJECT, new CastingRuleNullableStaticMethod(PARSE_FLOAT_OBJECT));
      rules.registerCastingRule(DOUBLE, new CastingRuleStaticMethod(PARSE_DOUBLE));
      rules.registerCastingRule(DOUBLEOBJECT, new CastingRuleNullableStaticMethod(PARSE_DOUBLE_OBJECT));
      rules.registerCastingRule(ANY, new CastingRuleNullableStaticMethod(JavaMethod.getStatic(this.getAnyClassName(environment), "valueOf", ANY, STRING)));
      if (followCasters) {
         this.constructExpansionCastingRules(environment, rules);
      }

   }

   public Type toASMType() {
      return this.type;
   }

   public int getNumberType() {
      return 0;
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      if (ExpressionStringMethod.hasMethod(name, environment.getEnvironment().getTypeRegistry())) {
         return new ExpressionStringMethod(position, value.eval(environment), name);
      } else {
         IPartialExpression result = this.memberExpansion(position, environment, value.eval(environment), name);
         if (result == null) {
            environment.error(position, "string value has no such member: " + name);
            return new ExpressionInvalid(position, ZenTypeAny.INSTANCE);
         } else {
            return result;
         }
      }
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return null;
   }

   public String getSignature() {
      return "Ljava/lang/String;";
   }

   public boolean isPointer() {
      return true;
   }

   public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      Expression result = this.unaryExpansion(position, environment, value, operator);
      if (result == null) {
         environment.error(position, "operator not supported on a string");
         return new ExpressionInvalid(position);
      } else {
         return result;
      }
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      if (operator != OperatorType.CAT && operator != OperatorType.ADD) {
         if (operator == OperatorType.INDEXGET) {
            return new ExpressionStringIndex(position, left, right.cast(position, environment, INT));
         } else if (operator == OperatorType.CONTAINS) {
            return new ExpressionStringContains(position, left, right.cast(position, environment, STRING));
         } else {
            Expression result = this.binaryExpansion(position, environment, left, right, operator);
            if (result == null) {
               environment.error(position, "operator not supported on strings");
               return new ExpressionInvalid(position, this);
            } else {
               return result;
            }
         }
      } else if (left instanceof ExpressionStringConcat) {
         ((ExpressionStringConcat)left).add(right.cast(position, environment, this));
         return left;
      } else if (right.getType().canCastImplicit(STRING, environment)) {
         List<Expression> values = new ArrayList();
         values.add(left);
         values.add(right.cast(position, environment, this));
         return new ExpressionStringConcat(position, values);
      } else {
         Expression expanded = this.binaryExpansion(position, environment, left, right, operator);
         if (expanded == null) {
            environment.error(position, "cannot add " + right.getType().getName() + " to a string");
            return new ExpressionInvalid(position, this);
         } else {
            return expanded;
         }
      }
   }

   public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      Expression result = this.trinaryExpansion(position, environment, first, second, third, operator);
      if (result == null) {
         environment.error(position, "operator not supported on strings");
         return new ExpressionInvalid(position, this);
      } else {
         return result;
      }
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      if (right.getType().canCastImplicit(STRING, environment)) {
         return new ExpressionCompareGeneric(position, new ExpressionCallVirtual(position, environment, STRING_COMPARETO, left, new Expression[]{right.cast(position, environment, STRING)}), type);
      } else {
         Expression result = this.binaryExpansion(position, environment, left, right, OperatorType.COMPARE);
         if (result == null) {
            environment.error(position, "cannot compare strings");
            return new ExpressionInvalid(position, BOOL);
         } else {
            return new ExpressionCompareGeneric(position, result, type);
         }
      }
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      environment.error(position, "Cannot call a string value");
      return new ExpressionInvalid(position, INSTANCE);
   }

   public Class toJavaClass() {
      return String.class;
   }

   public String getName() {
      return "string";
   }

   public String getAnyClassName(IEnvironmentGlobal environment) {
      if (!environment.containsClass("any.AnyString")) {
         environment.putClass("any.AnyString", new byte[0]);
         environment.putClass("any.AnyString", AnyClassWriter.construct(new ZenTypeString.AnyDefinitionString(environment), "any/AnyString", this.type));
      }

      return "any/AnyString";
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionNull(position);
   }

   private class AnyDefinitionString implements IAnyDefinition {
      private final IEnvironmentGlobal environment;

      private AnyDefinitionString(IEnvironmentGlobal environment) {
         this.environment = environment;
      }

      public void defineMembers(ClassVisitor output) {
         output.visitField(2, "value", "Ljava/lang/String;", (String)null, (Object)null);
         MethodOutput valueOf = new MethodOutput(output, 9, "valueOf", "(Ljava/lang/String;)" + ZenTypeUtil.signature(IAny.class), (String)null, (String[])null);
         valueOf.start();
         valueOf.newObject("any/AnyString");
         valueOf.dup();
         valueOf.loadObject(0);
         valueOf.construct("any/AnyString", "Ljava/lang/String;");
         valueOf.returnObject();
         valueOf.end();
         MethodOutput constructor = new MethodOutput(output, 1, "<init>", "(Ljava/lang/String;)V", (String)null, (String[])null);
         constructor.start();
         constructor.loadObject(0);
         constructor.invokeSpecial(ZenTypeUtil.internal(Object.class), "<init>", "()V");
         constructor.loadObject(0);
         constructor.load(ZenTypeString.this.type, 1);
         constructor.putField("any/AnyString", "value", "Ljava/lang/String;");
         constructor.returnType(Type.VOID_TYPE);
         constructor.end();
      }

      public void defineStaticCanCastImplicit(MethodOutput output) {
         Label lblOthers = new Label();
         output.constant(ZenTypeString.this.type);
         output.loadObject(0);
         output.ifACmpNe(lblOthers);
         output.iConst1();
         output.returnInt();
         output.label(lblOthers);
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeString.this.getName());
         if (expansion != null) {
            expansion.compileAnyCanCastImplicit(ZenType.STRING, output, this.environment, 0);
         }

         output.iConst0();
         output.returnInt();
      }

      public void defineStaticAs(MethodOutput output) {
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeString.this.getName());
         if (expansion != null) {
            expansion.compileAnyCast(ZenType.STRING, output, this.environment, 0, 1);
         }

         AnyClassWriter.throwCastException(output, "string", 1);
      }

      public void defineNot(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "string", "not");
      }

      public void defineNeg(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "string", "negate");
      }

      public void defineAdd(MethodOutput output) {
         this.defineCat(output);
      }

      public void defineSub(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "string", "-");
      }

      public void defineCat(MethodOutput output) {
         output.newObject(StringBuilder.class);
         output.dup();
         output.invokeSpecial(ZenTypeUtil.internal(StringBuilder.class), "<init>", "()V");
         this.getValue(output);
         output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, String.class);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASSTRING.invokeVirtual(output);
         output.invokeVirtual(StringBuilder.class, "append", StringBuilder.class, String.class);
         output.invokeVirtual(StringBuilder.class, "toString", String.class);
         output.invokeStatic("any/AnyString", "valueOf", "(Ljava/lang/String;)" + ZenTypeUtil.signature(IAny.class));
         output.returnObject();
      }

      public void defineMul(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "string", "*");
      }

      public void defineDiv(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "string", "/");
      }

      public void defineMod(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "string", "*");
      }

      public void defineAnd(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "string", "&");
      }

      public void defineOr(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "string", "|");
      }

      public void defineXor(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "string", "^");
      }

      public void defineRange(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "string", "..");
      }

      public void defineCompareTo(MethodOutput output) {
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASSTRING.invokeVirtual(output);
         output.invokeVirtual(String.class, "compareTo", Integer.TYPE, String.class);
         output.returnInt();
      }

      public void defineContains(MethodOutput output) {
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASSTRING.invokeVirtual(output);
         output.invokeVirtual(String.class, "contains", Boolean.TYPE, CharSequence.class);
         output.returnInt();
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
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASINT.invokeVirtual(output);
         output.dup();
         output.iConst1();
         output.iAdd();
         output.invokeVirtual(String.class, "substring", String.class, Integer.TYPE, Integer.TYPE);
         output.invokeStatic("any/AnyString", "valueOf", "(Ljava/lang/String;)" + ZenTypeUtil.signature(IAny.class));
         output.returnObject();
      }

      public void defineIndexSet(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "string", "[]=");
      }

      public void defineCall(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "string", "call");
      }

      public void defineAsBool(MethodOutput output) {
         this.getValue(output);
         output.invokeStatic(Boolean.class, "parseBoolean", Boolean.TYPE, String.class);
         output.returnInt();
      }

      public void defineAsByte(MethodOutput output) {
         this.getValue(output);
         output.invokeStatic(Byte.class, "parseByte", Byte.TYPE, String.class);
         output.returnInt();
      }

      public void defineAsShort(MethodOutput output) {
         this.getValue(output);
         output.invokeStatic(Short.class, "parseShort", Short.TYPE, String.class);
         output.returnInt();
      }

      public void defineAsInt(MethodOutput output) {
         this.getValue(output);
         output.invokeStatic(Integer.class, "parseInt", Integer.TYPE, String.class);
         output.returnInt();
      }

      public void defineAsLong(MethodOutput output) {
         this.getValue(output);
         output.invokeStatic(Long.class, "parseLong", Long.TYPE, String.class);
         output.returnType(Type.LONG_TYPE);
      }

      public void defineAsFloat(MethodOutput output) {
         this.getValue(output);
         output.invokeStatic(Float.class, "parseFloat", Float.TYPE, String.class);
         output.returnType(Type.FLOAT_TYPE);
      }

      public void defineAsDouble(MethodOutput output) {
         this.getValue(output);
         output.invokeStatic(Double.class, "parseDouble", Double.TYPE, String.class);
         output.returnType(Type.DOUBLE_TYPE);
      }

      public void defineAsString(MethodOutput output) {
         this.getValue(output);
         output.returnObject();
      }

      public void defineAs(MethodOutput output) {
         int localValue = output.local(ZenTypeString.this.type);
         this.getValue(output);
         output.store(ZenTypeString.this.type, localValue);
         TypeExpansion expansion = this.environment.getExpansion(ZenTypeString.this.getName());
         if (expansion != null) {
            expansion.compileAnyCast(ZenType.STRING, output, this.environment, localValue, 1);
         }

         AnyClassWriter.throwCastException(output, "string", 1);
      }

      public void defineIs(MethodOutput output) {
         Label lblEq = new Label();
         output.loadObject(1);
         output.constant(ZenTypeString.this.type);
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
         AnyClassWriter.throwUnsupportedException(output, "string", "iterator");
      }

      public void defineIteratorMulti(MethodOutput output) {
         AnyClassWriter.throwUnsupportedException(output, "string", "iterator");
      }

      private void getValue(MethodOutput output) {
         output.loadObject(0);
         output.getField("any/AnyString", "value", "Ljava/lang/String;");
      }

      public void defineEquals(MethodOutput output) {
         this.getValue(output);
         output.loadObject(1);
         AnyClassWriter.METHOD_ASSTRING.invokeVirtual(output);
         output.invokeVirtual(String.class, "equals", Boolean.TYPE, Object.class);
         output.returnInt();
      }

      public void defineHashCode(MethodOutput output) {
         this.getValue(output);
         output.invokeVirtual(String.class, "hashCode", Integer.TYPE);
         output.returnInt();
      }
   }
}
