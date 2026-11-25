package stanhebben.zenscript.expression.partial;

import java.lang.reflect.Type;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolJavaClass;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class PartialJavaClass implements IPartialExpression {
   private final ZenPosition position;
   private final Type cls;

   public PartialJavaClass(ZenPosition position, Type cls) {
      this.position = position;
      this.cls = cls;
   }

   public Expression eval(IEnvironmentGlobal environment) {
      environment.error(this.position, "cannot use class name as expression");
      return new ExpressionInvalid(this.position);
   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      environment.error(position, "cannot assign to a class name");
      return new ExpressionInvalid(position);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return environment.getType(this.cls).getStaticMember(position, environment, name);
   }

   public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
      environment.error(position, "cannot call a class name");
      return new ExpressionInvalid(position);
   }

   public IZenSymbol toSymbol() {
      return new SymbolJavaClass(this.cls);
   }

   public ZenType getType() {
      return null;
   }

   public ZenType toType(IEnvironmentGlobal environment) {
      return environment.getType(this.cls);
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return new ZenType[numArguments];
   }
}
