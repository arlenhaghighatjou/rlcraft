package stanhebben.zenscript.expression;

import java.util.Map;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAssociative;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionMapIndexGet extends Expression {
   private final Expression map;
   private final Expression index;
   private final ZenType type;

   public ExpressionMapIndexGet(ZenPosition position, Expression map, Expression index) {
      super(position);
      this.map = map;
      this.index = index;
      this.type = ((ZenTypeAssociative)map.getType()).getValueType();
   }

   public ZenType getType() {
      return this.type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         this.map.compile(result, environment);
         this.index.cast(this.getPosition(), environment, ((ZenTypeAssociative)this.map.getType()).getKeyType()).compile(result, environment);
         environment.getOutput().invokeInterface(ZenTypeUtil.internal(Map.class), "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
         environment.getOutput().checkCast(this.type.toASMType().getInternalName());
      }

   }
}
