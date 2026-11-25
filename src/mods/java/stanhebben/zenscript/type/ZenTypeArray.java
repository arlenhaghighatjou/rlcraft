package stanhebben.zenscript.type;

import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionArrayContains;
import stanhebben.zenscript.expression.ExpressionCompareGeneric;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionNull;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.util.ZenPosition;

public abstract class ZenTypeArray extends ZenType {
   private final ZenType base;
   private final String name;

   public ZenTypeArray(ZenType base) {
      this.base = base;
      this.name = base + "[]";
   }

   public ZenTypeArray(ZenType base, String name) {
      this.base = base;
      this.name = name;
   }

   public ZenType getBaseType() {
      return this.base;
   }

   public abstract IPartialExpression getMemberLength(ZenPosition var1, IEnvironmentGlobal var2, IPartialExpression var3);

   public abstract Expression indexGet(ZenPosition var1, IEnvironmentGlobal var2, Expression var3, Expression var4);

   public abstract Expression indexSet(ZenPosition var1, IEnvironmentGlobal var2, Expression var3, Expression var4, Expression var5);

   public abstract Expression add(ZenPosition var1, IEnvironmentGlobal var2, Expression var3, Expression var4);

   public final String getName() {
      return this.name;
   }

   public final boolean canCastExplicit(ZenType type, IEnvironmentGlobal environment) {
      return this.equals(type) || this.canCastExpansion(environment, type);
   }

   public final int getNumberType() {
      return 0;
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, IPartialExpression value, String name) {
      if (name.equals("length")) {
         return this.getMemberLength(position, environment, value);
      } else {
         IPartialExpression result = this.memberExpansion(position, environment, value.eval(environment), name);
         if (result == null) {
            environment.error(position, "no such member in array: " + name);
            return new ExpressionInvalid(position);
         } else {
            return result;
         }
      }
   }

   public final IPartialExpression getStaticMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      IPartialExpression result = this.staticMemberExpansion(position, environment, name);
      if (result == null) {
         environment.error(position, "no such member in array: " + name);
         return new ExpressionInvalid(position);
      } else {
         return result;
      }
   }

   public final boolean isPointer() {
      return true;
   }

   public final Expression unary(ZenPosition position, IEnvironmentGlobal environment, Expression value, OperatorType operator) {
      Expression result = this.unaryExpansion(position, environment, value, operator);
      if (result == null) {
         environment.error(position, "Array has no unary operators");
         return new ExpressionInvalid(position);
      } else {
         return result;
      }
   }

   public Expression binary(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, OperatorType operator) {
      switch(operator) {
      case INDEXGET:
         return this.indexGet(position, environment, left, right);
      case ADD:
         if (!this.getBaseType().equals(right.getType()) && !right.getType().canCastExplicit(this.getBaseType(), environment)) {
            throw new IllegalArgumentException(String.format("Cannot add %s to %s", right.getType().toString(), this.toString()));
         }

         return this.add(position, environment, left, right);
      case CONTAINS:
         return new ExpressionArrayContains(position, environment, left, right);
      default:
         Expression result = this.binaryExpansion(position, environment, left, right, operator);
         if (result == null) {
            environment.error(position, this.getName() + " doesn't have such operator");
            return new ExpressionInvalid(position);
         } else {
            return result;
         }
      }
   }

   public final Expression trinary(ZenPosition position, IEnvironmentGlobal environment, Expression first, Expression second, Expression third, OperatorType operator) {
      if (operator == OperatorType.INDEXSET) {
         return this.indexSet(position, environment, first, second, third);
      } else {
         Expression result = this.trinaryExpansion(position, environment, first, second, third, operator);
         if (result == null) {
            environment.error(position, this.getName() + " doesn't have such operator");
            return new ExpressionInvalid(position);
         } else {
            return result;
         }
      }
   }

   public final Expression compare(ZenPosition position, IEnvironmentGlobal environment, Expression left, Expression right, CompareType type) {
      Expression compare = this.binaryExpansion(position, environment, left, right, OperatorType.COMPARE);
      if (compare == null) {
         environment.error(position, "Arrays cannot be compared");
         return new ExpressionInvalid(position, this);
      } else {
         return new ExpressionCompareGeneric(position, compare, type);
      }
   }

   public final Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      environment.error(position, "Cannot call an array");
      return new ExpressionInvalid(position);
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionNull(position);
   }

   public String getNameForInterfaceSignature() {
      return this.base.getNameForInterfaceSignature() + "Array";
   }
}
