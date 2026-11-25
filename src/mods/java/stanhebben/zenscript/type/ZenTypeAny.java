package stanhebben.zenscript.type;

import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallVirtual;
import stanhebben.zenscript.expression.ExpressionCompareGeneric;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionNull;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.casting.CastingAnyBool;
import stanhebben.zenscript.type.casting.CastingAnyByte;
import stanhebben.zenscript.type.casting.CastingAnyDouble;
import stanhebben.zenscript.type.casting.CastingAnyFloat;
import stanhebben.zenscript.type.casting.CastingAnyInt;
import stanhebben.zenscript.type.casting.CastingAnyLong;
import stanhebben.zenscript.type.casting.CastingAnyShort;
import stanhebben.zenscript.type.casting.CastingAnyString;
import stanhebben.zenscript.type.casting.CastingRuleAnyAs;
import stanhebben.zenscript.type.casting.CastingRuleNullableStaticMethod;
import stanhebben.zenscript.type.casting.ICastingRule;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.AnyClassWriter;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;
import stanhebben.zenscript.value.IAny;

public class ZenTypeAny extends ZenType {
   public static final ZenTypeAny INSTANCE = new ZenTypeAny();

   private ZenTypeAny() {
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      environment.error(position, "any values not yet supported");
      return new ExpressionInvalid(position);
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      environment.error(position, "any values not yet supported");
      return new ExpressionInvalid(position);
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return null;
   }

   public ICastingRule getCastingRule(ZenType type, IEnvironmentGlobal environment) {
      ICastingRule base = super.getCastingRule(type, environment);
      return (ICastingRule)(base == null ? new CastingRuleAnyAs(type) : base);
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      rules.registerCastingRule(BOOL, CastingAnyBool.INSTANCE);
      rules.registerCastingRule(BOOLOBJECT, new CastingRuleNullableStaticMethod(BOOL_VALUEOF, CastingAnyBool.INSTANCE));
      rules.registerCastingRule(BYTE, CastingAnyByte.INSTANCE);
      rules.registerCastingRule(BYTEOBJECT, new CastingRuleNullableStaticMethod(BYTE_VALUEOF, CastingAnyByte.INSTANCE));
      rules.registerCastingRule(SHORT, CastingAnyShort.INSTANCE);
      rules.registerCastingRule(SHORTOBJECT, new CastingRuleNullableStaticMethod(SHORT_VALUEOF, CastingAnyShort.INSTANCE));
      rules.registerCastingRule(INT, CastingAnyInt.INSTANCE);
      rules.registerCastingRule(INTOBJECT, new CastingRuleNullableStaticMethod(INT_VALUEOF, CastingAnyInt.INSTANCE));
      rules.registerCastingRule(LONG, CastingAnyLong.INSTANCE);
      rules.registerCastingRule(LONGOBJECT, new CastingRuleNullableStaticMethod(LONG_VALUEOF, CastingAnyLong.INSTANCE));
      rules.registerCastingRule(FLOAT, CastingAnyFloat.INSTANCE);
      rules.registerCastingRule(FLOATOBJECT, new CastingRuleNullableStaticMethod(FLOAT_VALUEOF, CastingAnyFloat.INSTANCE));
      rules.registerCastingRule(DOUBLE, CastingAnyDouble.INSTANCE);
      rules.registerCastingRule(DOUBLEOBJECT, new CastingRuleNullableStaticMethod(DOUBLE_VALUEOF, CastingAnyDouble.INSTANCE));
      rules.registerCastingRule(STRING, CastingAnyString.INSTANCE);
      if (followCasters) {
         this.constructExpansionCastingRules(environment, rules);
      }

   }

   public boolean canCastExplicit(ZenType type, IEnvironmentGlobal environment) {
      return true;
   }

   public Class toJavaClass() {
      return IAny.class;
   }

   public Type toASMType() {
      return Type.getType(IAny.class);
   }

   public int getNumberType() {
      return 0;
   }

   public String getSignature() {
      return ZenTypeUtil.signature(IAny.class);
   }

   public boolean isPointer() {
      return true;
   }

   public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      switch(operator) {
      case NEG:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_NEG, value, new Expression[0]);
      case NOT:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_NOT, value, new Expression[0]);
      default:
         return new ExpressionInvalid(position, INSTANCE);
      }
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      switch(operator) {
      case ADD:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_ADD, left, new Expression[]{right.cast(position, environment, ANY)});
      case CAT:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_CAT, left, new Expression[]{right.cast(position, environment, ANY)});
      case SUB:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_SUB, left, new Expression[]{right.cast(position, environment, ANY)});
      case MUL:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_MUL, left, new Expression[]{right.cast(position, environment, ANY)});
      case DIV:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_DIV, left, new Expression[]{right.cast(position, environment, ANY)});
      case MOD:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_MOD, left, new Expression[]{right.cast(position, environment, ANY)});
      case AND:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_AND, left, new Expression[]{right.cast(position, environment, ANY)});
      case OR:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_OR, left, new Expression[]{right.cast(position, environment, ANY)});
      case XOR:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_XOR, left, new Expression[]{right.cast(position, environment, ANY)});
      case CONTAINS:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_CONTAINS, left, new Expression[]{right.cast(position, environment, ANY)});
      case INDEXGET:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_INDEXGET, left, new Expression[]{right.cast(position, environment, ANY)});
      case RANGE:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_RANGE, left, new Expression[]{right.cast(position, environment, ANY)});
      case COMPARE:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_COMPARETO, left, new Expression[]{right.cast(position, environment, ANY)});
      case MEMBERGETTER:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_MEMBERGET, left, new Expression[]{right.cast(position, environment, STRING)});
      default:
         return new ExpressionInvalid(position, ANY);
      }
   }

   public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      switch(operator) {
      case INDEXSET:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_INDEXSET, first, new Expression[]{second.cast(position, environment, ANY), third.cast(position, environment, ANY)});
      case MEMBERSETTER:
         return new ExpressionCallVirtual(position, environment, AnyClassWriter.METHOD_MEMBERSET, first, new Expression[]{second.cast(position, environment, STRING), third.cast(position, environment, ANY)});
      default:
         return new ExpressionInvalid(position, ANY);
      }
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      Expression comparator = new ExpressionCallVirtual(position, environment, JavaMethod.get(environment, IAny.class, "compareTo", IAny.class), left, new Expression[]{right});
      return new ExpressionCompareGeneric(position, comparator, type);
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      return new ExpressionCallVirtual(position, environment, JavaMethod.get(environment, IAny.class, "call", IAny[].class), receiver, arguments);
   }

   public ZenType[] predictCallTypes(int numArguments) {
      ZenType[] result = new ZenType[numArguments];

      for(int i = 0; i < result.length; ++i) {
         result[i] = ANY;
      }

      return result;
   }

   public String getName() {
      return "any";
   }

   public String getAnyClassName(IEnvironmentGlobal environment) {
      throw new UnsupportedOperationException("Cannot get any class name from the any type. That's like trying to stuff a freezer into a freezer.");
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionNull(position);
   }
}
