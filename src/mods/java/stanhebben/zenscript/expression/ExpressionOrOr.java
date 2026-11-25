package stanhebben.zenscript.expression;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionOrOr extends Expression {
   private final Expression a;
   private final Expression b;

   public ExpressionOrOr(ZenPosition position, Expression a, Expression b) {
      super(position);
      this.a = a;
      this.b = b;
   }

   public ZenType getType() {
      return this.a.getType();
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      MethodOutput output = environment.getOutput();
      Label onTrue = new Label();
      Label end = new Label();
      this.a.compile(true, environment);
      output.ifNE(onTrue);
      this.b.compile(true, environment);
      output.ifNE(onTrue);
      output.iConst0();
      output.goTo(end);
      output.label(onTrue);
      output.iConst1();
      output.label(end);
      if (!result) {
         output.pop();
      }

   }
}
