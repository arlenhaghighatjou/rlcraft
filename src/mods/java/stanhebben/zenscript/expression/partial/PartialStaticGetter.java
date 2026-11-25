package stanhebben.zenscript.expression.partial;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.symbols.SymbolJavaStaticGetter;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.util.ZenPosition;

public class PartialStaticGetter implements IPartialExpression {
   private final ZenPosition position;
   private final IJavaMethod method;

   public PartialStaticGetter(ZenPosition position, IJavaMethod method) {
      this.position = position;
      this.method = method;
   }

   public Expression eval(IEnvironmentGlobal environment) {
      return new ExpressionCallStatic(this.position, environment, this.method, new Expression[0]);
   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      environment.error(position, "cannot alter this final");
      return new ExpressionInvalid(position);
   }

   public IPartialExpression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      return this.method.getReturnType().getMember(position, environment, this, name);
   }

   public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
      environment.error(position, "value cannot be called");
      return new ExpressionInvalid(position);
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return new ZenType[numArguments];
   }

   public IZenSymbol toSymbol() {
      return new SymbolJavaStaticGetter(this.method);
   }

   public ZenType getType() {
      return this.method.getReturnType();
   }

   public ZenType toType(IEnvironmentGlobal environment) {
      environment.error(this.position, "not a valid type");
      return ZenType.ANY;
   }
}
