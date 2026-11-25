package stanhebben.zenscript.expression.partial;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionGlobalGet;
import stanhebben.zenscript.expression.ExpressionGlobalSet;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolGlobalValue;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class PartialGlobalValue implements IPartialExpression {
   private final SymbolGlobalValue value;

   public PartialGlobalValue(SymbolGlobalValue symbolGlobalValue) {
      this.value = symbolGlobalValue;
   }

   public Expression eval(IEnvironmentGlobal environment) {
      return new ExpressionGlobalGet(this.value);
   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      return new ExpressionGlobalSet(this.value, other);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return this.value.getType().getMember(position, environment, this, name);
   }

   public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
      return this.value.getType().call(position, environment, this.eval(environment), values);
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return this.value.getType().predictCallTypes(numArguments);
   }

   public IZenSymbol toSymbol() {
      return this.value;
   }

   public ZenType getType() {
      return this.value.getType();
   }

   public ZenType toType(IEnvironmentGlobal environment) {
      return this.value.getType();
   }

   public String getName() {
      return this.value.getName();
   }
}
