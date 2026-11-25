package stanhebben.zenscript.expression.partial;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolType;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeNative;
import stanhebben.zenscript.type.ZenTypeZenClass;
import stanhebben.zenscript.util.ZenPosition;

public class PartialType implements IPartialExpression {
   private final ZenPosition position;
   private final ZenType type;

   public PartialType(ZenPosition position, ZenType type) {
      this.position = position;
      this.type = type;
   }

   public Expression eval(IEnvironmentGlobal environment) {
      environment.error(this.position, "cannot use type as expression");
      return new ExpressionInvalid(this.position, this.type);
   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      environment.error(position, "cannot assign to a type");
      return new ExpressionInvalid(position, this.type);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return this.type.getStaticMember(position, environment, name);
   }

   public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
      if (!(this.type instanceof ZenTypeNative) && !(this.type instanceof ZenTypeZenClass)) {
         environment.error(position, "cannot call a type");
         return new ExpressionInvalid(position, this.type);
      } else {
         return this.type.call(position, environment, (Expression)null, values);
      }
   }

   public IZenSymbol toSymbol() {
      return new SymbolType(this.type);
   }

   public ZenType getType() {
      return null;
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return new ZenType[numArguments];
   }

   public ZenType toType(IEnvironmentGlobal environment) {
      return this.type;
   }
}
