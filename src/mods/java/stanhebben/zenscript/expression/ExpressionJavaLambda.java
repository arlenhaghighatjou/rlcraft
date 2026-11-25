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

public class ExpressionJavaLambda extends Expression {
   private final Class<?> interfaceClass;
   private final List<ParsedFunctionArgument> arguments;
   private final List<Statement> statements;
   private final ZenType type;

   public ExpressionJavaLambda(ZenPosition position, Class<?> interfaceClass, List<ParsedFunctionArgument> arguments, List<Statement> statements, ZenType type) {
      super(position);
      this.interfaceClass = interfaceClass;
      this.arguments = arguments;
      this.statements = statements;
      this.type = type;
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
            cw.visit(50, 1, clsName, (String)null, "java/lang/Object", new String[]{ZenTypeUtil.internal(this.interfaceClass)});
            MethodOutput output = new MethodOutput(cw, 1, method.getName(), ZenTypeUtil.descriptor(method), (String)null, (String[])null);
            output.position(this.getPosition());
            IEnvironmentClass environmentClass = new EnvironmentClass(cw, environment);
            EnvironmentMethodLambda environmentMethod = new EnvironmentMethodLambda(output, environmentClass, clsName);
            int i = 0;

            for(int j = 0; i < this.arguments.size(); ++i) {
               environmentMethod.putValue(((ParsedFunctionArgument)this.arguments.get(i)).getName(), new SymbolArgument(i + j + 1, environment.getType(method.getGenericParameterTypes()[i])), this.getPosition());
               if (environment.getType(method.getGenericParameterTypes()[i]).isLarge()) {
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
}
