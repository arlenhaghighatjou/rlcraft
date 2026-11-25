package stanhebben.zenscript.type;

import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionNull;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.casting.CastingRuleNullableStaticMethod;
import stanhebben.zenscript.type.casting.CastingRuleNullableVirtualMethod;
import stanhebben.zenscript.type.casting.CastingRuleVirtualMethod;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ZenTypeLongObject extends ZenType {
   public static final ZenTypeLongObject INSTANCE = new ZenTypeLongObject();

   private ZenTypeLongObject() {
   }

   public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      return LONG.unary(position, environment, value.cast(position, environment, LONG), operator);
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      return LONG.binary(position, environment, left.cast(position, environment, LONG), right, operator);
   }

   public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      return LONG.trinary(position, environment, first.cast(position, environment, LONG), second, third, operator);
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      return LONG.compare(position, environment, left.cast(position, environment, LONG), right, type);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      return LONG.getMember(position, environment, value.eval(environment).cast(position, environment, LONG), name);
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return LONG.getStaticMember(position, environment, name);
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      return LONG.call(position, environment, receiver.cast(position, environment, LONG), arguments);
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return LONG.makeIterator(numValues, methodOutput);
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      rules.registerCastingRule(BYTE, new CastingRuleVirtualMethod(BYTE_VALUE));
      rules.registerCastingRule(BYTEOBJECT, new CastingRuleNullableStaticMethod(BYTE_VALUEOF, new CastingRuleVirtualMethod(BYTE_VALUE)));
      rules.registerCastingRule(SHORT, new CastingRuleVirtualMethod(SHORT_VALUE));
      rules.registerCastingRule(SHORTOBJECT, new CastingRuleNullableStaticMethod(SHORT_VALUEOF, new CastingRuleVirtualMethod(SHORT_VALUE)));
      rules.registerCastingRule(INT, new CastingRuleVirtualMethod(INT_VALUE));
      rules.registerCastingRule(INTOBJECT, new CastingRuleNullableStaticMethod(INT_VALUEOF, new CastingRuleVirtualMethod(INT_VALUE)));
      rules.registerCastingRule(LONG, new CastingRuleVirtualMethod(LONG_VALUE));
      rules.registerCastingRule(LONGOBJECT, new CastingRuleNullableStaticMethod(LONG_VALUEOF, new CastingRuleVirtualMethod(LONG_VALUE)));
      rules.registerCastingRule(FLOAT, new CastingRuleVirtualMethod(FLOAT_VALUE));
      rules.registerCastingRule(FLOATOBJECT, new CastingRuleNullableStaticMethod(FLOAT_VALUEOF, new CastingRuleVirtualMethod(FLOAT_VALUE)));
      rules.registerCastingRule(DOUBLE, new CastingRuleVirtualMethod(DOUBLE_VALUE));
      rules.registerCastingRule(DOUBLEOBJECT, new CastingRuleNullableStaticMethod(DOUBLE_VALUEOF, new CastingRuleVirtualMethod(DOUBLE_VALUE)));
      rules.registerCastingRule(STRING, new CastingRuleNullableVirtualMethod(LONGOBJECT, LONG_TOSTRING));
      rules.registerCastingRule(ANY, new CastingRuleNullableStaticMethod(JavaMethod.getStatic(this.getAnyClassName(environment), "valueOf", ANY, LONG), new CastingRuleVirtualMethod(LONG_VALUE)));
      if (followCasters) {
         this.constructExpansionCastingRules(environment, rules);
      }

   }

   public Class toJavaClass() {
      return Long.class;
   }

   public Type toASMType() {
      return Type.getType(Long.class);
   }

   public int getNumberType() {
      return 4;
   }

   public String getSignature() {
      return ZenTypeUtil.signature(Long.class);
   }

   public boolean isPointer() {
      return true;
   }

   public String getName() {
      return "long?";
   }

   public String getAnyClassName(IEnvironmentGlobal environment) {
      return LONG.getAnyClassName(environment);
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionNull(position);
   }
}
