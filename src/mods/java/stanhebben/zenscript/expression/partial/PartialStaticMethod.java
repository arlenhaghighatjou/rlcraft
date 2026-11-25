package stanhebben.zenscript.expression.partial;

import java.util.Arrays;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolJavaStaticMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.util.ZenPosition;

public class PartialStaticMethod implements IPartialExpression {
   private final ZenPosition position;
   private final IJavaMethod method;

   public PartialStaticMethod(ZenPosition position, IJavaMethod method) {
      this.position = position;
      this.method = method;
   }

   public Expression eval(IEnvironmentGlobal environment) {
      environment.error(this.position, "not a valid expression");
      return new ExpressionInvalid(this.position);
   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      environment.error(position, "cannot alter this final");
      return new ExpressionInvalid(position);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      environment.error(position, "methods have no members");
      return new ExpressionInvalid(position);
   }

   public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
      if (this.method.accepts(environment, values)) {
         return new ExpressionCallStatic(position, environment, this.method, values);
      } else {
         environment.error(position, "parameter count mismatch: got " + values.length + " arguments");
         return new ExpressionInvalid(position, this.method.getReturnType());
      }
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return (ZenType[])Arrays.copyOf(this.method.getParameterTypes(), numArguments);
   }

   public IZenSymbol toSymbol() {
      return new SymbolJavaStaticMethod(this.method);
   }

   public ZenType getType() {
      return this.method.getReturnType();
   }

   public ZenType toType(IEnvironmentGlobal environment) {
      return ZenType.ANY;
   }
}
