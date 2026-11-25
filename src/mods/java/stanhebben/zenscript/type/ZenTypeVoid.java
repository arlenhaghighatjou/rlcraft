package stanhebben.zenscript.type;

import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.util.ZenPosition;

public class ZenTypeVoid extends ZenType {
   public static final ZenTypeVoid INSTANCE = new ZenTypeVoid();

   private ZenTypeVoid() {
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      environment.error(position, "void doesn't have members");
      return new ExpressionInvalid(position, this);
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      environment.error(position, "void doesn't have static members");
      return new ExpressionInvalid(position, this);
   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return null;
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
   }

   public Type toASMType() {
      return Type.VOID_TYPE;
   }

   public int getNumberType() {
      return 0;
   }

   public String getSignature() {
      return "V";
   }

   public boolean isPointer() {
      return false;
   }

   public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      environment.error(position, "void does not have operators");
      return new ExpressionInvalid(position, this);
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      environment.error(position, "void does not have operators");
      return new ExpressionInvalid(position, this);
   }

   public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      environment.error(position, "void does not have operators");
      return new ExpressionInvalid(position, this);
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      environment.error(position, "void does not have operators");
      return new ExpressionInvalid(position, this);
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      environment.error(position, "cannot call a void");
      return new ExpressionInvalid(position, this);
   }

   public Class toJavaClass() {
      return Void.TYPE;
   }

   public String getName() {
      return "void";
   }

   public String getAnyClassName(IEnvironmentGlobal environment) {
      throw new UnsupportedOperationException("Cannot convert void to anything, not even any");
   }

   public Expression defaultValue(ZenPosition position) {
      throw new RuntimeException("void has no default value");
   }
}
