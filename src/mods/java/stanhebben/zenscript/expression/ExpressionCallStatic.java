package stanhebben.zenscript.expression;

import java.util.Collections;
import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ArrayUtil;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.Pair;
import stanhebben.zenscript.util.ZenPosition;

public class ExpressionCallStatic extends Expression {
   private final IJavaMethod method;
   private Expression[] arguments;
   private final IEnvironmentGlobal environmentGlobal;
   private final List<Pair<ZenType, ParsedExpression>> filledDefaultValues;

   public ExpressionCallStatic(ZenPosition position, IEnvironmentGlobal environment, IJavaMethod method, List<Pair<ZenType, ParsedExpression>> filledDefaultValues, Expression... arguments) {
      super(position);
      this.method = method;
      this.environmentGlobal = environment;
      this.arguments = arguments;
      this.filledDefaultValues = filledDefaultValues;
   }

   public ExpressionCallStatic(ZenPosition position, IEnvironmentGlobal environment, IJavaMethod method, Expression... arguments) {
      this(position, environment, method, Collections.emptyList(), arguments);
   }

   public ZenType getType() {
      return this.method.getReturnType();
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      MethodOutput output = environment.getOutput();

      for(Pair<ZenType, ParsedExpression> filledDefaultValue : this.filledDefaultValues) {
         this.arguments = (Expression[])ArrayUtil.add(this.arguments, ((ParsedExpression)filledDefaultValue.getValue()).compileKey(environment, filledDefaultValue.getKey()));
      }

      Expression[] rematchedArguments = JavaMethod.rematch(this.getPosition(), this.method, this.environmentGlobal, this.arguments);

      for(Expression argument : rematchedArguments) {
         argument.compile(true, environment);
      }

      this.method.invokeStatic(output);
      if (this.method.getReturnType() != ZenType.VOID && !result) {
         output.pop(this.method.getReturnType().isLarge());
      }

   }
}
