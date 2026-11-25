package stanhebben.zenscript.expression;

import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayList;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionArrayListGet extends Expression {
   private final Expression array;
   private final Expression index;
   private final ZenType type;
   private final ZenPosition position;

   public ExpressionArrayListGet(ZenPosition position, Expression array, Expression index) {
      super(position);
      this.array = array;
      this.index = index;
      this.type = ((ZenTypeArrayList)array.getType()).getBaseType();
      this.position = position;
   }

   public ZenType getType() {
      return this.type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         this.array.compile(result, environment);
         this.index.compile(result, environment);
         environment.getOutput().invokeInterface(List.class, "get", Object.class, Integer.TYPE);
         environment.getOutput().checkCast(ZenTypeUtil.checkPrimitive(this.type).toASMType().getInternalName());
         if (ZenTypeUtil.isPrimitive(this.type)) {
            ZenTypeUtil.checkPrimitive(this.type).getCastingRule(this.type, environment).compile(environment);
         }
      }

   }

   public Expression assign(ZenPosition position, IEnvironmentGlobal environment, Expression other) {
      return new ExpressionArrayListSet(position, this.array, this.index, other);
   }
}
