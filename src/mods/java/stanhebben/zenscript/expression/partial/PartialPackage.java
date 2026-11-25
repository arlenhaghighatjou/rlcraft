package stanhebben.zenscript.expression.partial;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolPackage;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class PartialPackage implements IPartialExpression {
   private final ZenPosition position;
   private final SymbolPackage contents;

   public PartialPackage(ZenPosition position, SymbolPackage contents) {
      this.position = position;
      this.contents = contents;
   }

   public Expression eval(IEnvironmentGlobal environment) {
      environment.error(this.position, "Cannot use package name as expression");
      return new ExpressionInvalid(this.position);
   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      environment.error(position, "Cannot assign to a package");
      return new ExpressionInvalid(position);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      IZenSymbol member = this.contents.get(name);
      if (member == null) {
         environment.error(position, "No such member: " + name);
         return new ExpressionInvalid(position);
      } else {
         return member.instance(position);
      }
   }

   public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
      environment.error(position, "cannot call a package");
      return new ExpressionInvalid(position);
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return new ZenType[numArguments];
   }

   public IZenSymbol toSymbol() {
      return null;
   }

   public ZenType getType() {
      return null;
   }

   public ZenType toType(IEnvironmentGlobal environment) {
      environment.error(this.position, "not a valid type");
      return ZenType.ANY;
   }
}
