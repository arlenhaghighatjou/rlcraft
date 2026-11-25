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

public class ZenTypeBoolObject extends ZenType {
   public static final ZenTypeBoolObject INSTANCE = new ZenTypeBoolObject();

   private ZenTypeBoolObject() {
   }

   public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      return BOOL.unary(position, environment, value.cast(position, environment, ZenType.BOOL), operator);
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      return BOOL.binary(position, environment, left.cast(position, environment, BOOL), right, operator);
   }

   public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      return BOOL.trinary(position, environment, first.cast(position, environment, BOOL), second, third, operator);
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      return BOOL.compare(position, environment, left.cast(position, environment, BOOL), right, type);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      return BOOL.getMember(position, environment, value.eval(environment).cast(position, environment, BOOL), name);
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return BOOL.getStaticMember(position, environment, name);
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      return BOOL.call(position, environment, receiver.cast(position, environment, BOOL), arguments);
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return BOOL.makeIterator(numValues, methodOutput);
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      rules.registerCastingRule(BOOL, new CastingRuleVirtualMethod(BOOL_VALUE));
      rules.registerCastingRule(STRING, new CastingRuleNullableVirtualMethod(BOOL, BOOL_TOSTRING));
      rules.registerCastingRule(ANY, new CastingRuleNullableStaticMethod(JavaMethod.getStatic(BOOL.getAnyClassName(environment), "valueOf", ANY, BOOL), new CastingRuleVirtualMethod(BOOL_VALUE)));
   }

   public boolean canCastExplicit(ZenType type, IEnvironmentGlobal environment) {
      return this.canCastImplicit(type, environment);
   }

   public Class toJavaClass() {
      return Boolean.class;
   }

   public Type toASMType() {
      return Type.getType(Boolean.class);
   }

   public int getNumberType() {
      return 0;
   }

   public String getSignature() {
      return ZenTypeUtil.signature(Boolean.class);
   }

   public boolean isPointer() {
      return true;
   }

   public String getName() {
      return "bool?";
   }

   public String getAnyClassName(IEnvironmentGlobal environment) {
      return BOOL.getAnyClassName(environment);
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionNull(position);
   }
}
