package stanhebben.zenscript.expression.partial;

import java.util.Arrays;
import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class PartialExpansionCall extends PartialStaticGenerated {
   private final Expression instance;

   public PartialExpansionCall(ZenPosition position, String owner, String method, String signature, List<ParsedFunctionArgument> arguments, ZenType returnType, Expression instance) {
      super(position, owner, method, signature, arguments, returnType);
      this.instance = instance;
   }

   public Expression call(ZenPosition position, IEnvironmentMethod environment, Expression... values) {
      Expression[] expressions = (Expression[])Arrays.copyOf(new Expression[]{this.instance}, values.length + 1);
      System.arraycopy(values, 0, expressions, 1, values.length);
      return super.call(position, environment, expressions);
   }
}
