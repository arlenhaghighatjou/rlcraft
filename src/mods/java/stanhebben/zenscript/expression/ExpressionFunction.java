package stanhebben.zenscript.expression;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.EnvironmentClass;
import stanhebben.zenscript.compiler.EnvironmentMethodLambda;
import stanhebben.zenscript.compiler.IEnvironmentClass;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.compiler.ZenClassWriter;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.symbols.SymbolArgument;
import stanhebben.zenscript.symbols.SymbolCaptured;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAny;
import stanhebben.zenscript.type.ZenTypeFunction;
import stanhebben.zenscript.type.ZenTypeFunctionCallable;
import stanhebben.zenscript.type.ZenTypeNative;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionFunction extends Expression {
   private final List<ParsedFunctionArgument> arguments;
   private final ZenType returnType;
   private final List<Statement> statements;
   private final ZenTypeFunctionCallable functionType;
   private final String className;

   public ExpressionFunction(ZenPosition position, List<ParsedFunctionArgument> arguments, ZenType returnType, List<Statement> statements, String className) {
      super(position);
      System.out.println("Function expression: " + arguments.size() + " arguments");
      this.arguments = arguments;
      this.returnType = returnType;
      this.statements = statements;
      ZenType[] argumentTypes = new ZenType[arguments.size()];

      for(int i = 0; i < arguments.size(); ++i) {
         argumentTypes[i] = ((ParsedFunctionArgument)arguments.get(i)).getType();
      }

      this.className = className;
      this.functionType = new ZenTypeFunctionCallable(returnType, argumentTypes, className, this.makeDescriptor());
   }

   public Expression cast(ZenPosition position, IEnvironmentGlobal environment, ZenType type) {
      if (type instanceof ZenTypeNative) {
         ZenTypeNative nativeType = (ZenTypeNative)type;
         Class<?> nativeClass = nativeType.getNativeClass();
         Method method = ZenTypeUtil.findFunctionalInterfaceMethod(nativeClass);
         if (method != null) {
            if (this.returnType != ZenTypeAny.INSTANCE && !this.returnType.canCastImplicit(environment.getType(method.getGenericReturnType()), environment)) {
               environment.error(position, "return type not compatible");
               return new ExpressionInvalid(position);
            } else if (this.arguments.size() != method.getParameterTypes().length) {
               environment.error(this.getPosition(), String.format("Expected %s arguments, received %s arguments", method.getParameterTypes().length, this.arguments.size()));
               return new ExpressionInvalid(position);
            } else {
               for(int i = 0; i < this.arguments.size(); ++i) {
                  ZenType argumentType = environment.getType(method.getGenericParameterTypes()[i]);
                  if (((ParsedFunctionArgument)this.arguments.get(i)).getType() != ZenTypeAny.INSTANCE && !argumentType.canCastImplicit(((ParsedFunctionArgument)this.arguments.get(i)).getType(), environment)) {
                     environment.error(position, "argument " + i + " doesn't match");
                     return new ExpressionInvalid(position);
                  }
               }

               return new ExpressionJavaLambda(position, nativeClass, this.arguments, this.statements, environment.getType(nativeClass));
            }
         } else {
            environment.error(position, type.toString() + " is not a functional interface");
            return new ExpressionInvalid(position);
         }
      } else if (!(type instanceof ZenTypeFunction)) {
         environment.error(position, "Cannot cast a function literal to " + type.toString());
         return new ExpressionInvalid(position);
      } else {
         boolean matches = this.returnType.equals(((ZenTypeFunction)type).getReturnType());
         ZenType[] args = ((ZenTypeFunction)type).getArgumentTypes();
         if (matches) {
            for(int i = 0; i < this.arguments.size(); ++i) {
               matches &= ((ParsedFunctionArgument)this.arguments.get(i)).getType().equals(args[i]);
            }
         }

         if (matches) {
            return this;
         } else {
            environment.error(position, "Cannot cast a function literal to " + type.toString());
            return new ExpressionInvalid(position);
         }
      }
   }

   public ZenType getType() {
      return this.functionType;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         this.functionType.writeInterfaceClass(environment);
         ClassWriter cw = new ZenClassWriter(2);
         cw.visit(50, 1, this.className, (String)null, "java/lang/Object", new String[]{this.functionType.getInterfaceName()});
         cw.visitSource(this.getPosition().getFileName(), (String)null);
         MethodOutput output = new MethodOutput(cw, 1, "accept", this.functionType.getDescriptor(), (String)null, (String[])null);
         IEnvironmentClass environmentClass = new EnvironmentClass(cw, environment);
         EnvironmentMethodLambda environmentMethod = new EnvironmentMethodLambda(output, environmentClass, this.className);
         int i = 0;

         for(int j = 0; i < this.arguments.size(); ++i) {
            environmentMethod.putValue(((ParsedFunctionArgument)this.arguments.get(i)).getName(), new SymbolArgument(i + 1 + j, ((ParsedFunctionArgument)this.arguments.get(i)).getType()), this.getPosition());
            if (((ParsedFunctionArgument)this.arguments.get(i)).getType().isLarge()) {
               ++j;
            }
         }

         output.start();

         for(Statement statement : this.statements) {
            statement.compile(environmentMethod);
         }

         output.ret();
         output.end();
         environmentMethod.createConstructor(cw);
         environment.putClass(this.className, cw.toByteArray());
         environment.getOutput().newObject(this.className);
         environment.getOutput().dup();
         String[] arguments = (String[])environmentMethod.getCapturedVariables().stream().map(SymbolCaptured::getEvaluated).peek((expression) -> {
            expression.compile(true, environment);
         }).map(IPartialExpression::getType).map(ZenType::toASMType).map(Type::getDescriptor).toArray((x$0) -> {
            return new String[x$0];
         });
         environment.getOutput().construct(this.className, arguments);
      }
   }

   private String makeDescriptor() {
      StringBuilder sb = new StringBuilder("(");
      this.arguments.stream().map(ParsedFunctionArgument::getType).map(ZenType::getSignature).forEach(sb::append);
      sb.append(")").append(this.returnType.getSignature());
      return sb.toString();
   }

   public String getClassName() {
      return this.className;
   }

   public String getDescriptor() {
      return this.functionType.getDescriptor();
   }
}
