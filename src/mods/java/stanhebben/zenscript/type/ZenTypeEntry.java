package stanhebben.zenscript.type;

import java.util.Map.Entry;
import org.objectweb.asm.Type;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionBool;
import stanhebben.zenscript.expression.ExpressionEntryGet;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionNull;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.casting.ICastingRuleDelegate;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ZenTypeEntry extends ZenType {
   private final ZenType keyType;
   private final ZenType valueType;

   public ZenTypeEntry(ZenType keyType, ZenType valueType) {
      this.keyType = keyType;
      this.valueType = valueType;
   }

   public ZenTypeEntry(ZenTypeAssociative mapType) {
      this.keyType = mapType.getKeyType();
      this.valueType = mapType.getValueType();
   }

   public Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      Expression result = this.unaryExpansion(position, environment, value, operator);
      if (result == null) {
         environment.error(position, "Entries don't have unary operators");
         return new ExpressionInvalid(position);
      } else {
         return result;
      }
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      Expression result = this.binaryExpansion(position, environment, left, right, operator);
      if (result == null) {
         environment.error(position, "Entries don't have binary operators");
         return new ExpressionInvalid(position);
      } else {
         return result;
      }
   }

   public Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      Expression result = this.trinaryExpansion(position, environment, first, second, third, operator);
      if (result == null) {
         environment.error(position, "Entries don't have ternary operators");
         return new ExpressionInvalid(position);
      } else {
         return result;
      }
   }

   public Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      return new ExpressionBool(position, false);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      if (!name.equals("key") && !name.equals("value")) {
         IPartialExpression result = this.memberExpansion(position, environment, value.eval(environment), name);
         if (result == null) {
            environment.error(position, "Entries don't have a member called '" + name + "'!");
            return new ExpressionInvalid(position);
         } else {
            return result;
         }
      } else {
         return new ExpressionEntryGet(position, value.eval(environment), name.equals("key"));
      }
   }

   public IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      IPartialExpression result = this.staticMemberExpansion(position, environment, name);
      if (result == null) {
         environment.error(position, "Entries don't have a static member called '" + name + "'!");
         return new ExpressionInvalid(position);
      } else {
         return result;
      }
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      environment.error(position, "Cannot call an Entry");
      return new ExpressionInvalid(position);
   }

   public void constructCastingRules(IEnvironmentGlobal environment, ICastingRuleDelegate rules, boolean followCasters) {
      if (followCasters) {
         this.constructExpansionCastingRules(environment, rules);
      }

   }

   public IZenIterator makeIterator(int numValues, IEnvironmentMethod methodOutput) {
      return null;
   }

   public Class toJavaClass() {
      return Entry.class;
   }

   public Type toASMType() {
      return Type.getType(this.toJavaClass());
   }

   public int getNumberType() {
      return 0;
   }

   public String getSignature() {
      return ZenTypeUtil.signature(this.toJavaClass());
   }

   public boolean isPointer() {
      return false;
   }

   public String getAnyClassName(IEnvironmentGlobal environment) {
      return null;
   }

   public String getName() {
      return String.format("MapEntry_from_%s_to_%s_", this.keyType.getNameForInterfaceSignature(), this.valueType.getNameForInterfaceSignature());
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionNull(position);
   }

   public ZenType getKeyType() {
      return this.keyType;
   }

   public ZenType getValueType() {
      return this.valueType;
   }
}
