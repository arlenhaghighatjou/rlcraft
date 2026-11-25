package stanhebben.zenscript.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.ZenClassWriter;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionFunctionCall;
import stanhebben.zenscript.expression.ExpressionInvalid;
import stanhebben.zenscript.expression.ExpressionNull;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.util.Pair;
import stanhebben.zenscript.util.ZenPosition;

public class ZenTypeFunctionCallable extends ZenTypeFunction {
   private final String className;
   private final String descriptor;
   private final String interfaceName;

   public ZenTypeFunctionCallable(ZenType returnType, List<ParsedFunctionArgument> arguments, String className, String descriptor) {
      super(returnType, arguments);
      this.className = className;
      this.descriptor = descriptor;
      this.interfaceName = makeInterfaceName(returnType, this.argumentTypes);
   }

   public ZenTypeFunctionCallable(ZenType returnType, ZenType[] argumentTypes, String className, String descriptor) {
      super(returnType, argumentTypes);
      this.className = className;
      this.descriptor = descriptor;
      this.interfaceName = makeInterfaceName(returnType, argumentTypes);
   }

   public ZenTypeFunctionCallable(ZenType returnType, ZenType[] argumentTypes, String className) {
      super(returnType, argumentTypes);
      this.className = className;
      StringBuilder sb = new StringBuilder("(");
      Arrays.stream(argumentTypes).map(ZenType::getSignature).forEach(sb::append);
      sb.append(")").append(returnType.getSignature());
      this.descriptor = sb.toString();
      this.interfaceName = makeInterfaceName(returnType, argumentTypes);
   }

   public static String makeInterfaceName(ZenType returnType, ZenType[] argumentTypes) {
      StringJoiner stringJoiner = new StringJoiner("_");

      for(ZenType argumentType : argumentTypes) {
         stringJoiner.add(argumentType.getNameForInterfaceSignature());
      }

      stringJoiner.add("to");
      stringJoiner.add(returnType.getNameForInterfaceSignature());
      stringJoiner.add("generated_interface");
      return stringJoiner.toString();
   }

   public String getClassName() {
      return this.className;
   }

   public String getSignature() {
      return "L" + this.getInterfaceName() + ";";
   }

   public Expression call(ZenPosition position, IEnvironmentGlobal environment, Expression receiver, Expression... arguments) {
      ParsedExpression[] filledDefaultExpressions = (ParsedExpression[])Arrays.copyOfRange(this.defaultExpressions, arguments.length, this.argumentTypes.length);
      if (Arrays.stream(filledDefaultExpressions).anyMatch(Objects::isNull)) {
         environment.error(position, "Expected " + this.argumentTypes.length + " parameters but got " + arguments.length);
         return new ExpressionInvalid(position, this.returnType);
      } else {
         Expression[] expressions = new Expression[this.argumentTypes.length];

         for(int i = 0; i < arguments.length; ++i) {
            expressions[i] = arguments[i].cast(position, environment, this.argumentTypes[i]);
         }

         ZenType[] filledDefaultExpressionTypes = (ZenType[])Arrays.copyOfRange(this.argumentTypes, arguments.length, this.argumentTypes.length);
         List<Pair<ZenType, ParsedExpression>> collect = new ArrayList();

         for(int i = 0; i < filledDefaultExpressions.length; ++i) {
            collect.add(new Pair(filledDefaultExpressionTypes[i], filledDefaultExpressions[i]));
         }

         return new ExpressionFunctionCall(position, expressions, collect, this.returnType, receiver, this.interfaceName, this.descriptor);
      }
   }

   public ZenType[] predictCallTypes(int numArguments) {
      return (ZenType[])Arrays.copyOf(this.argumentTypes, numArguments);
   }

   public Class toJavaClass() {
      return null;
   }

   public Type toASMType() {
      return Type.getType(this.getSignature());
   }

   public String getInterfaceName() {
      return this.interfaceName;
   }

   public Expression defaultValue(ZenPosition position) {
      return new ExpressionNull(position);
   }

   public void writeInterfaceClass(IEnvironmentGlobal environment) {
      if (!environment.containsClass(this.interfaceName)) {
         ClassWriter cw = new ZenClassWriter(2);
         cw.visit(50, 5633, this.interfaceName, (String)null, "java/lang/Object", new String[0]);
         cw.visitSource("generated_interface", (String)null);
         MethodVisitor accept = cw.visitMethod(1025, "accept", this.descriptor, (String)null, (String[])null);
         accept.visitEnd();
         cw.visitEnd();
         environment.putClass(this.interfaceName, cw.toByteArray());
      }
   }

   public String getDescriptor() {
      return this.descriptor;
   }

   public String getNameForInterfaceSignature() {
      return this.interfaceName;
   }
}
