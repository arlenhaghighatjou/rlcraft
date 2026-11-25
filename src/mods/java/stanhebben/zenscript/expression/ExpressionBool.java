package stanhebben.zenscript.expression;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionBool extends Expression {
   private final boolean value;

   public ExpressionBool(ZenPosition position, boolean value) {
      super(position);
      this.value = value;
   }

   public Expression cast(ZenPosition position, IEnvironmentGlobal environment, ZenType type) {
      return (Expression)(type == ZenType.BOOL ? this : super.cast(position, environment, type));
   }

   public Expression getMember(ZenPosition position, IEnvironmentGlobal environment, String name) {
      environment.error(position, "Bool constants do not have members");
      return new ExpressionInvalid(position, ZenType.BOOL);
   }

   public ZenType getType() {
      return ZenType.BOOL;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         if (this.value) {
            environment.getOutput().iConst1();
         } else {
            environment.getOutput().iConst0();
         }
      }

   }

   public void compileIf(Label onElse, IEnvironmentMethod environment) {
      if (!this.value) {
         environment.getOutput().goTo(onElse);
      }

   }
}
