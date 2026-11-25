package stanhebben.zenscript.expression;

import java.util.Collection;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionArrayListRemove extends Expression {
   private final Expression list;
   private final Expression val;

   public ExpressionArrayListRemove(ZenPosition position, Expression list, Expression val) {
      super(position);
      this.list = list;
      this.val = val;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.list.compile(true, environment);
      this.val.cast(this.getPosition(), environment, ZenTypeUtil.checkPrimitive(this.val.getType())).compile(true, environment);
      environment.getOutput().invokeInterface(Collection.class, "remove", Boolean.TYPE, Object.class);
      if (!result) {
         environment.getOutput().pop();
      }

   }

   public ZenType getType() {
      return ZenType.BOOL;
   }
}
