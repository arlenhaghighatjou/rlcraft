package stanhebben.zenscript.expression;

import java.util.ArrayList;
import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeFunction;
import stanhebben.zenscript.type.natives.JavaMethod;
import stanhebben.zenscript.util.ZenPosition;

/** @deprecated */
@Deprecated
public class ExpressionJavaMethodStatic extends Expression {
   private final JavaMethod method;
   private final ZenType type;

   public ExpressionJavaMethodStatic(ZenPosition position, JavaMethod method, IEnvironmentGlobal environment) {
      super(position);
      this.method = method;
      List<ParsedFunctionArgument> arguments = new ArrayList();

      for(int i = 0; i < method.getParameterTypes().length; ++i) {
         arguments.add(new ParsedFunctionArgument("p" + i, environment.getType(method.getMethod().getGenericParameterTypes()[i])));
      }

      this.type = new ZenTypeFunction(environment.getType(method.getMethod().getGenericReturnType()), arguments);
   }

   public ZenType getType() {
      return this.type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
   }
}
