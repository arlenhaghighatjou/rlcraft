package stanhebben.zenscript.expression;

import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionCallVirtual extends Expression {
   private final IJavaMethod method;
   private final Expression receiver;
   private final Expression[] arguments;

   public ExpressionCallVirtual(ZenPosition position, IEnvironmentGlobal environment, IJavaMethod method, Expression receiver, Expression... arguments) {
      super(position);
      this.method = method;
      this.receiver = receiver;
      this.arguments = JavaMethod.rematch(position, method, environment, arguments);
   }

   public ZenType getType() {
      return this.method instanceof JavaMethod && ((JavaMethod)this.method).returnsSelf ? this.receiver.getType() : this.method.getReturnType();
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.receiver.compile(true, environment);

      for(Expression argument : this.arguments) {
         argument.compile(true, environment);
      }

      this.method.invokeVirtual(environment.getOutput());
      if (!result && this.method.getReturnType() != ZenType.VOID) {
         environment.getOutput().pop(this.method.getReturnType().isLarge());
      } else if (this.method instanceof JavaMethod && ((JavaMethod)this.method).returnsSelf) {
         environment.getOutput().checkCast(this.receiver.getType().toASMType().getInternalName());
      }

   }
}
