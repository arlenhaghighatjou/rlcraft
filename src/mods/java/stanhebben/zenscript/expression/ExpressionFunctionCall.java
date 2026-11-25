package stanhebben.zenscript.expression;

import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.Pair;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionFunctionCall extends Expression {
   private final Expression[] values;
   private final ZenType returnType;
   private final Expression receiver;
   private final String className;
   private final String descriptor;
   private final List<Pair<ZenType, ParsedExpression>> filledDefaultValues;

   public ExpressionFunctionCall(ZenPosition position, Expression[] values, List<Pair<ZenType, ParsedExpression>> filledDefaultValues, ZenType returnType, Expression receiver, String className, String descriptor) {
      super(position);
      this.values = values;
      this.filledDefaultValues = filledDefaultValues;
      this.returnType = returnType;
      this.receiver = receiver;
      this.className = className;
      this.descriptor = descriptor;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      this.receiver.compile(true, environment);

      for(Expression value : this.values) {
         value.compile(true, environment);
      }

      for(Pair<ZenType, ParsedExpression> filledDefaultValue : this.filledDefaultValues) {
         ((ParsedExpression)filledDefaultValue.getValue()).compileKey(environment, filledDefaultValue.getKey()).compile(true, environment);
      }

      environment.getOutput().invokeInterface(this.className, "accept", this.descriptor);
      if (this.returnType != ZenType.VOID && !result) {
         environment.getOutput().pop(this.returnType.isLarge());
      }

   }

   public ZenType getType() {
      return this.returnType;
   }
}
