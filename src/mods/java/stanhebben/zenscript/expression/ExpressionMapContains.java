package stanhebben.zenscript.expression;

import java.util.Map;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAssociative;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionMapContains extends Expression {
   private final Expression map;
   private final Expression key;

   public ExpressionMapContains(ZenPosition position, Expression map, Expression key) {
      super(position);
      this.map = map;
      this.key = key;
   }

   public ZenType getType() {
      return ZenType.BOOL;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         ZenTypeAssociative mapType = (ZenTypeAssociative)this.map.getType();
         this.map.compile(result, environment);
         this.key.cast(this.getPosition(), environment, mapType.getKeyType()).compile(result, environment);
         environment.getOutput().invokeInterface(ZenTypeUtil.internal(Map.class), "containsKey", "(Ljava/lang/Object;)Z");
      }

   }
}
