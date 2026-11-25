package stanhebben.zenscript.expression;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.EnvironmentClass;
import stanhebben.zenscript.compiler.EnvironmentMethodLambda;
import stanhebben.zenscript.compiler.IEnvironmentClass;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.compiler.ZenClassWriter;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.symbols.SymbolArgument;
import stanhebben.zenscript.symbols.SymbolCaptured;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import stanhebben.zenscript.util.ZenPosition;
import stanhebben.zenscript.util.ZenTypeUtil;

public class ExpressionJavaLambdaSimpleGeneric extends Expression {
   private final Class interfaceClass;
   public Class genericClass;
   private final List<ParsedFunctionArgument> arguments;
   private final List<Statement> statements;
   private final String descriptor;
   private final ZenType type;

   public ExpressionJavaLambdaSimpleGeneric(ZenPosition position, Class<?> interfaceClass, List<ParsedFunctionArgument> arguments, List<Statement> statements, ZenType type) {
      super(position);
      this.interfaceClass = interfaceClass;
      this.arguments = arguments;
      this.statements = statements;
      this.type = type;
      ZenType genericType = ((ParsedFunctionArgument)arguments.get(0)).getType();
      this.genericClass = genericType.equals(ZenType.ANY) ? Object.class : genericType.toJavaClass();
      Method method = ZenTypeUtil.findFunctionalInterfaceMethod(interfaceClass);
      if (method == null) {
         throw new IllegalArgumentException("Internal error: Cannot create function for " + interfaceClass + " because it is not a functional interface!");
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append("(");

         for(int i = 0; i < arguments.size(); ++i) {
            ZenType t = ((ParsedFunctionArgument)arguments.get(i)).getType();
            if (t.equals(ZenType.ANY)) {
               sb.append(ZenTypeUtil.signature(method.getParameterTypes()[i]));
            } else {
               sb.append(t.getSignature());
            }
         }

         sb.append(")").append(ZenTypeUtil.signature(interfaceClass.getDeclaredMethods()[0].getReturnType()));
         this.descriptor = sb.toString();
      }
   }

   public ZenType getType() {
      return this.type;
   }

   public void compile(boolean result, IEnvironmentMethod environment) {
      if (result) {
         Method method = ZenTypeUtil.findFunctionalInterfaceMethod(this.interfaceClass);
         if (method == null) {
            environment.error("Internal error: Cannot create function for " + this.interfaceClass + " because it is not a functional interface!");
         } else {
            String clsName = environment.makeClassNameWithMiddleName(this.getPosition().getFile().getClassName());
            ClassWriter cw = new ZenClassWriter(2);
            cw.visitSource(this.getPosition().getFileName(), (String)null);
            cw.visit(50, 1, clsName, this.createMethodSignature(), "java/lang/Object", new String[]{ZenTypeUtil.internal(this.interfaceClass)});
            MethodOutput output = new MethodOutput(cw, 1, method.getName(), this.descriptor, (String)null, (String[])null);
            output.position(this.getPosition());
            IEnvironmentClass environmentClass = new EnvironmentClass(cw, environment);
            EnvironmentMethodLambda environmentMethod = new EnvironmentMethodLambda(output, environmentClass, clsName);
            int i = 0;

            for(int j = 0; i < this.arguments.size(); ++i) {
               ZenType typeToPut = ((ParsedFunctionArgument)this.arguments.get(i)).getType();
               if (typeToPut.equals(ZenType.ANY)) {
                  typeToPut = environment.getType(method.getGenericParameterTypes()[i]);
               }

               if (typeToPut == null) {
                  typeToPut = environment.getType(method.getParameterTypes()[i]);
               }

               environmentMethod.putValue(((ParsedFunctionArgument)this.arguments.get(i)).getName(), new SymbolArgument(i + 1 + j, typeToPut), this.getPosition());
               if (typeToPut.isLarge()) {
                  ++j;
               }
            }

            output.start();

            for(Statement statement : this.statements) {
               statement.compile(environmentMethod);
            }

            output.ret();
            output.end();
            if (!Objects.equals(this.genericClass, Object.class)) {
               MethodOutput bridge = new MethodOutput(cw, 4161, method.getName(), ZenTypeUtil.descriptor(method), (String)null, (String[])null);
               bridge.loadObject(0);
               bridge.loadObject(1);
               bridge.checkCast(ZenTypeUtil.internal(this.genericClass));
               if (this.arguments.size() > 1) {
                  int i = 1;

                  while(i < this.arguments.size()) {
                     Type var10001 = Type.getType(method.getParameterTypes()[i]);
                     ++i;
                     bridge.load(var10001, i);
                  }
               }

               bridge.invokeVirtual(clsName, method.getName(), this.descriptor);
               bridge.returnType(Type.getReturnType(method));
               bridge.end();
            }

            environmentMethod.createConstructor(cw);
            environment.putClass(clsName, cw.toByteArray());
            environment.getOutput().newObject(clsName);
            environment.getOutput().dup();
            String[] arguments = (String[])environmentMethod.getCapturedVariables().stream().map(SymbolCaptured::getEvaluated).peek((expression) -> {
               expression.compile(true, environment);
            }).map(IPartialExpression::getType).map(ZenType::toASMType).map(Type::getDescriptor).toArray((x$0) -> {
               return new String[x$0];
            });
            environment.getOutput().construct(clsName, arguments);
         }
      }
   }

   private String createMethodSignature() {
      StringBuilder sb = new StringBuilder();
      sb.append("Ljava/lang/Object;");
      sb.append(ZenTypeUtil.signature(this.interfaceClass));
      sb.deleteCharAt(sb.length() - 1);
      sb.append("<").append(ZenTypeUtil.signature(this.genericClass)).append(">").append(";");
      return sb.toString();
   }
}
