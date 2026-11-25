package stanhebben.zenscript.expression;

import java.util.ArrayList;
import java.util.Collection;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeArrayList;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionArrayList extends Expression {
   private final Expression[] contents;
   private final ZenTypeArrayList type;

   public ExpressionArrayList(ZenPosition position, ZenTypeArrayList type, Expression[] contents) {
      super(position);
      this.contents = contents;
      this.type = type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         MethodOutput methodOutput = environment.getOutput();
         methodOutput.newObject(ArrayList.class);
         methodOutput.dup();
         methodOutput.invokeSpecial("java/util/ArrayList", "<init>", "()V");

         for(Expression content : this.contents) {
            methodOutput.dup();
            content.cast(this.getPosition(), environment, ZenTypeUtil.checkPrimitive(content.getType())).compile(true, environment);
            methodOutput.invokeInterface(Collection.class, "add", Boolean.TYPE, Object.class);
            methodOutput.pop();
         }

      }
   }

   public ZenType getType() {
      return this.type;
   }
}
