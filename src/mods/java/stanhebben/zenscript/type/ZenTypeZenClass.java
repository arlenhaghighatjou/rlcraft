package stanhebben.zenscript.type;

import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.zenclasses.ParsedZenClass;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionNull;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.util.ZenPosition;

public class ZenTypeZenClass extends ZenType {
   public final ParsedZenClass zenClass;

   public ZenTypeZenClass(ParsedZenClass zenClass) {
      this.zenClass = zenClass;
   }

   public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      throw new UnsupportedOperationException("Cannot use unary operators for ZenClasses");
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      throw new UnsupportedOperationException("Classes have no binary operators");
   }

   public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      throw new UnsupportedOperationException("Classes don't have ternary operators");
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      throw new UnsupportedOperationException("Why would you compare friggin classes?");
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      return this.zenClass.getMember(position, environment, value, name, false);
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return this.zenClass.getMember(position, environment, (IPartialExpression)null, name, true);
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      return this.zenClass.call(position, environment, arguments);
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return null;
   }

   public Class toJavaClass() {
      return this.zenClass.thisClass;
   }

   public Type toASMType() {
      return this.toJavaClass() != null ? Type.getType(this.toJavaClass()) : Type.getType(this.getSignature());
   }

   public int getNumberType() {
      return 0;
   }

   public String getSignature() {
      return "L" + this.zenClass.className + ";";
   }

   public boolean isPointer() {
      return false;
   }

   public String getAnyClassName(IEnvironmentGlobal environment) {
      return this.zenClass.className;
   }

   public String getName() {
      return this.zenClass.className;
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionNull(position);
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return this.zenClass.predictCallTypes(numArguments);
   }
}
