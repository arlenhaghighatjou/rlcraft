package stanhebben.zenscript.expression;

import java.util.Map;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeInt;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionMapSize extends Expression {
   private final Expression map;

   public ExpressionMapSize(ZenPosition position, Expression map) {
      super(position);
      this.map = map;
   }

   public ZenType getType() {
      return ZenTypeInt.INSTANCE;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         this.map.compile(true, environment);
         environment.getOutput().invokeInterface(ZenTypeUtil.internal(Map.class), "size", "()I");
      }

   }
}
