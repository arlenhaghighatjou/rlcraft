package stanhebben.zenscript.expression.partial;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionLocalGet;
import stanhebben.zenscript.expression.ExpressionLocalSet;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolLocal;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class PartialLocal implements IPartialExpression {
   private final ZenPosition position;
   private final SymbolLocal variable;

   public PartialLocal(ZenPosition position, SymbolLocal variable) {
      this.position = position;
      this.variable = variable;
   }

   public Expression eval(IEnvironmentGlobal environment) {
      return new ExpressionLocalGet(this.position, this.variable);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return this.variable.getType().getMember(position, environment, this, name);
   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      if (this.variable.isFinal()) {
         environment.error(position, "value cannot be changed");
         return new ExpressionInvalid(position);
      } else {
         return new ExpressionLocalSet(position, this.variable, other);
      }
   }

   public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
      return this.variable.getType().call(position, environment, this.eval(environment), values);
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return this.variable.getType().predictCallTypes(numArguments);
   }

   public IZenSymbol toSymbol() {
      return this.variable;
   }

   public ZenType getType() {
      return this.variable.getType();
   }

   public ZenType toType(IEnvironmentGlobal environment) {
      environment.error(this.position, "not a valid type");
      return ZenType.ANY;
   }
}
