package stanhebben.zenscript.expression;

import java.util.Map;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAssociative;
import stanhebben.zenscript.type.ZenTypeVoid;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionMapIndexSet extends Expression {
   private final Expression map;
   private final Expression index;
   private final Expression value;
   private final ZenType type;

   public ExpressionMapIndexSet(ZenPosition position, Expression map, Expression index, Expression value) {
      super(position);
      this.map = map;
      this.index = index;
      this.value = value;
      this.type = ZenTypeVoid.INSTANCE;
   }

   public ZenType getType() {
      return this.type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         this.map.compile(result, environment);
         this.index.cast(this.getPosition(), environment, ((ZenTypeAssociative)this.map.getType()).getKeyType()).compile(result, environment);
         this.value.cast(this.getPosition(), environment, ((ZenTypeAssociative)this.map.getType()).getValueType()).compile(result, environment);
         environment.getOutput().invokeInterface(ZenTypeUtil.internal(Map.class), "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
         environment.getOutput().pop();
      }

   }
}
