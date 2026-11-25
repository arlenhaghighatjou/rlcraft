package stanhebben.zenscript.expression;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionAndAnd extends Expression {
   private final Expression a;
   private final Expression b;

   public ExpressionAndAnd(ZenPosition position, Expression a, Expression b) {
      super(position);
      this.a = a;
      this.b = b;
   }

   public ZenType getType() {
      return this.a.getType();
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      MethodOutput output = environment.getOutput();
      Label onFalse = new Label();
      Label end = new Label();
      this.a.compile(true, environment);
      output.ifEQ(onFalse);
      this.b.compile(true, environment);
      output.ifEQ(onFalse);
      output.iConst1();
      output.goTo(end);
      output.label(onFalse);
      output.iConst0();
      output.label(end);
      if (!result) {
         output.pop();
      }

   }

   public void compileIf(Label onElse, IEnvironmentMethod environment) {
      this.a.compile(true, environment);
      environment.getOutput().ifEQ(onElse);
      this.b.compile(true, environment);
      environment.getOutput().ifEQ(onElse);
   }
}
