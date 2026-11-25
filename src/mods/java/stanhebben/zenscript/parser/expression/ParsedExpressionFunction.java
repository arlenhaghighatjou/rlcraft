package stanhebben.zenscript.parser.expression;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.ExpressionFunction;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionJavaLambda;
import stanhebben.zenscript.expression.ExpressionJavaLambdaSimpleGeneric;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAny;
import stanhebben.zenscript.type.ZenTypeFunctionCallable;
import stanhebben.zenscript.type.ZenTypeNative;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ParsedExpressionFunction extends ParsedExpression {
   private final ZenType returnType;
   private final List<ParsedFunctionArgument> arguments;
   private final List<Statement> statements;

   public ParsedExpressionFunction(ZenPosition position, ZenType returnType, List<ParsedFunctionArgument> arguments, List<Statement> statements) {
      super(position);
      this.returnType = returnType;
      this.arguments = arguments;
      this.statements = statements;
   }

   public IPartialExpression compile(IEnvironmentMethod environment, ZenType predictedType) {
      if (predictedType instanceof ZenTypeNative) {
         System.out.println("Known predicted function type: " + predictedType);
         ZenTypeNative nativeType = (ZenTypeNative)predictedType;
         Class<?> nativeClass = nativeType.getNativeClass();
         Method method = ZenTypeUtil.findFunctionalInterfaceMethod(nativeClass);
         if (method != null) {
            if (this.returnType != ZenTypeAny.INSTANCE && !this.returnType.canCastImplicit(environment.getType(method.getGenericReturnType()), environment)) {
               environment.error(this.getPosition(), "return type not compatible");
               return new ExpressionInvalid(this.getPosition());
            } else if (this.arguments.size() != method.getParameterTypes().length) {
               environment.error(this.getPosition(), String.format("Expected %s arguments, received %s arguments", method.getParameterTypes().length, this.arguments.size()));
               return new ExpressionInvalid(this.getPosition());
            } else {
               for(int i = 0; i < this.arguments.size(); ++i) {
                  ZenType argumentType = environment.getType(method.getParameterTypes()[i]);
                  if (((ParsedFunctionArgument)this.arguments.get(i)).getType() != ZenTypeAny.INSTANCE && !((ParsedFunctionArgument)this.arguments.get(i)).getType().canCastImplicit(argumentType, environment)) {
                     environment.error(this.getPosition(), "argument " + i + " doesn't match");
                     return new ExpressionInvalid(this.getPosition());
                  }
               }

               return (IPartialExpression)(this.isGeneric(method) ? new ExpressionJavaLambdaSimpleGeneric(this.getPosition(), nativeClass, this.arguments, this.statements, environment.getType(nativeClass)) : new ExpressionJavaLambda(this.getPosition(), nativeClass, this.arguments, this.statements, environment.getType(nativeClass)));
            }
         } else {
            environment.error(this.getPosition(), predictedType.toString() + " is not a functional interface");
            return new ExpressionInvalid(this.getPosition());
         }
      } else if (predictedType instanceof ZenTypeFunctionCallable) {
         return new ExpressionFunction(this.getPosition(), this.arguments, this.returnType, this.statements, environment.makeClassNameWithMiddleName(((ZenTypeFunctionCallable)predictedType).getClassName()));
      } else {
         System.out.println("No known predicted type");
         return new ExpressionFunction(this.getPosition(), this.arguments, this.returnType, this.statements, environment.makeClassNameWithMiddleName(this.getPosition().getFile().getClassName()));
      }
   }

   private boolean isGeneric(Method method) {
      for(Type type : method.getGenericParameterTypes()) {
         if (type.getTypeName().equals("T")) {
            return true;
         }
      }

      return false;
   }
}
