package stanhebben.zenscript.expression;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionConditional extends Expression {
   private final Expression condition;
   private final Expression onIf;
   private final Expression onElse;

   public ExpressionConditional(ZenPosition position, Expression condition, Expression onIf, Expression onElse) {
      super(position);
      this.condition = condition;
      this.onIf = onIf;
      this.onElse = onElse;
   }

   public ZenType getType() {
      return this.onIf.getType();
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      Label lblElse = new Label();
      Label lblExit = new Label();
      this.condition.compileIf(lblElse, environment);
      this.onIf.cast(this.getPosition(), environment, this.getType()).compile(result, environment);
      environment.getOutput().goTo(lblExit);
      environment.getOutput().label(lblElse);
      this.onElse.cast(this.getPosition(), environment, this.getType()).compile(result, environment);
      environment.getOutput().label(lblExit);
   }
}
