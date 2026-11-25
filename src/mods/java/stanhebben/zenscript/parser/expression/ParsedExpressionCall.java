package stanhebben.zenscript.parser.expression;

import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpressionCall extends ParsedExpression {
   private final ParsedExpression receiver;
   private final List<ParsedExpression> arguments;

   public ParsedExpressionCall(ZenPosition position, ParsedExpression receiver, List<ParsedExpression> arguments) {
      super(position);
      this.receiver = receiver;
      this.arguments = arguments;
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      IPartialExpression cReceiver = this.receiver.compile(environment, predictedType);
      ZenType[] predictedTypes = cReceiver.predictCallTypes(this.arguments.size());
      Expression[] cArguments = new Expression[this.arguments.size()];

      for(int i = 0; i < cArguments.length; ++i) {
         IPartialExpression cArgument = ((ParsedExpression)this.arguments.get(i)).compile(environment, predictedTypes[i]);
         cArguments[i] = cArgument.eval(environment);
      }

      return cReceiver.call(this.getPosition(), environment, cArguments);
   }
}
