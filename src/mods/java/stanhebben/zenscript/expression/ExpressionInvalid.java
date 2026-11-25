package stanhebben.zenscript.expression;

import org.objectweb.asm.Label;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAny;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionInvalid extends Expression {
   private final ZenType type;

   public ExpressionInvalid(ZenPosition position) {
      super(position);
      this.type = ZenTypeAny.INSTANCE;
   }

   public ExpressionInvalid(ZenPosition position, ZenType type) {
      super(position);
      this.type = (ZenType)(type == null ? ZenType.ANY : type);
   }

   public Expression getMember(ZenPosition position, IEnvironmentGlobal errors, String name) {
      return new ExpressionInvalid(position);
   }

   public Expression cast(ZenPosition position, IEnvironmentGlobal errors, ZenType type) {
      return new ExpressionInvalid(position, type);
   }

   public ZenType getType() {
      return this.type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.type.defaultValue(this.getPosition()).compile(result, environment);
   }

   public void compileIf(Label onElse, IEnvironmentMethod environment) {
      environment.getOutput().goTo(onElse);
   }
}
