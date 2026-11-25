package stanhebben.zenscript.expression.partial;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionNothing;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeZenClass;
import stanhebben.zenscript.util.ZenPosition;

public class PartialZSClass implements IPartialExpression {
   private final ZenTypeZenClass type;

   public PartialZSClass(ZenTypeZenClass type) {
      this.type = type;
   }

   public Expression eval(IEnvironmentGlobal environment) {
      return new ExpressionNothing(this.type.zenClass.position);
   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      throw new UnsupportedOperationException("Cannot assign to a class");
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return this.type.getMember(position, environment, this, name);
   }

   public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
      return this.type.call(position, environment, this.eval(environment), values);
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return this.type.predictCallTypes(numArguments);
   }

   public IZenSymbol toSymbol() {
      return (position) -> {
         return this;
      };
   }

   public ZenType getType() {
      return this.type;
   }

   public ZenType toType(IEnvironmentGlobal environment) {
      return this.type;
   }
}
