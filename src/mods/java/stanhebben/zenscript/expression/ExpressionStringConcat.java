package stanhebben.zenscript.expression;

import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeString;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionStringConcat extends Expression {
   private final List<Expression> values;

   public ExpressionStringConcat(ZenPosition position, List<Expression> values) {
      super(position);
      this.values = values;
   }

   public void add(Expression value) {
      this.values.add(value);
   }

   public ZenType getType() {
      return ZenTypeString.INSTANCE;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         MethodOutput output = environment.getOutput();
         output.newObject(StringBuilder.class);
         output.dup();
         output.construct(StringBuilder.class);

         for(Expression value : this.values) {
            value.compile(true, environment);
            output.invoke(StringBuilder.class, "append", StringBuilder.class, String.class);
         }

         output.invoke(StringBuilder.class, "toString", String.class);
      } else {
         for(Expression value : this.values) {
            value.compile(false, environment);
         }
      }

   }
}
