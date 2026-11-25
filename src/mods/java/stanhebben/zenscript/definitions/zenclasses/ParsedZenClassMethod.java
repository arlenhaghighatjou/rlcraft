package stanhebben.zenscript.definitions.zenclasses;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;
import org.objectweb.asm.ClassVisitor;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.EnvironmentMethod;
import stanhebben.zenscript.compiler.EnvironmentScript;
import stanhebben.zenscript.compiler.IEnvironmentClass;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.ParsedFunction;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.parser.expression.ParsedExpressionVariable;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.statements.StatementReturn;
import stanhebben.zenscript.symbols.SymbolArgument;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAny;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.ZenNativeMember;
import stanhebben.zenscript.util.MethodOutput;

public class ParsedZenClassMethod {
   final ParsedFunction method;
   final String className;

   public ParsedZenClassMethod(ParsedFunction parse, String className) {
      this.method = parse;
      this.className = className;
   }

   public static ParsedZenClassMethod parse(ZenTokener parser, EnvironmentScript classEnvironment, String className) {
      Token tName = parser.required(1, "identifier expected");
      parser.required(30, "( expected");
      List<ParsedFunctionArgument> arguments = new ArrayList();
      if (parser.optional(31) == null) {
         Token argName = parser.required(1, "identifier expected");
         ZenType type = ZenTypeAny.INSTANCE;
         ParsedExpression expression = null;
         boolean hasDefaultArgument = false;
         if (parser.optional(120) != null) {
            type = ZenType.read(parser, classEnvironment);
         }

         if (parser.optional(39) != null) {
            expression = ParsedExpression.read(parser, classEnvironment);
            if (expression instanceof ParsedExpressionVariable) {
               throw new ParseException(parser.getFile(), parser.getLine(), parser.getLineOffset(), "Variables are not allowed in default arguments");
            }

            hasDefaultArgument = true;
         }

         arguments.add(new ParsedFunctionArgument(argName.getValue(), type, expression));

         Token argName2;
         ZenType type2;
         ParsedExpression expression2;
         for(; parser.optional(11) != null; arguments.add(new ParsedFunctionArgument(argName2.getValue(), type2, expression2))) {
            argName2 = parser.required(1, "identifier expected");
            type2 = ZenTypeAny.INSTANCE;
            expression2 = null;
            if (parser.optional(120) != null) {
               type2 = ZenType.read(parser, classEnvironment);
            }

            if (parser.optional(39) != null) {
               expression2 = ParsedExpression.read(parser, classEnvironment);
               if (expression2 instanceof ParsedExpressionVariable) {
                  throw new ParseException(parser.getFile(), parser.getLine(), parser.getLineOffset(), "Variables are not allowed in default arguments");
               }

               hasDefaultArgument = true;
            } else if (hasDefaultArgument) {
               throw new ParseException(parser.getFile(), parser.getLine(), parser.getLineOffset(), "Parameter " + argName2.getValue() + " requires a default argument");
            }
         }

         parser.required(31, ") expected");
      }

      ZenType type = ZenTypeAny.INSTANCE;
      if (parser.optional(120) != null) {
         type = ZenType.read(parser, classEnvironment);
      }

      parser.required(5, "{ expected");
      Statement[] statements;
      if (parser.optional(6) != null) {
         statements = new Statement[0];
      } else {
         ArrayList<Statement> statementsAL = new ArrayList();

         while(parser.optional(6) == null) {
            statementsAL.add(Statement.read(parser, classEnvironment, type));
         }

         statements = (Statement[])statementsAL.toArray(new Statement[statementsAL.size()]);
      }

      return new ParsedZenClassMethod(new ParsedFunction(tName.getPosition(), tName.getValue(), arguments, type, statements), className);
   }

   public void addToMember(ZenNativeMember zenNativeMember) {
      zenNativeMember.addMethod(new ParsedZenClassMethod.ZenClassMethod());
   }

   public void writeAll(ClassVisitor newClass, IEnvironmentClass environmentNewClass) {
      String description = this.method.getSignature();
      MethodOutput methodOutput = new MethodOutput(newClass, 1, this.method.getName(), description, (String)null, (String[])null);
      IEnvironmentMethod methodEnvironment = new EnvironmentMethod(methodOutput, environmentNewClass);
      List<ParsedFunctionArgument> arguments = this.method.getArguments();
      int i = 0;
      int j = 0;

      while(i < arguments.size()) {
         ParsedFunctionArgument argument = (ParsedFunctionArgument)arguments.get(i);
         String var10001 = argument.getName();
         ++i;
         methodEnvironment.putValue(var10001, new SymbolArgument(i + j, argument.getType()), this.method.getPosition());
         if (argument.getType().isLarge()) {
            ++j;
         }
      }

      methodOutput.start();
      Statement[] statements = this.method.getStatements();

      for(Statement statement : statements) {
         statement.compile(methodEnvironment);
      }

      if (this.method.getReturnType() != ZenType.VOID) {
         if (statements.length != 0 && statements[statements.length - 1] instanceof StatementReturn) {
            if (((StatementReturn)statements[statements.length - 1]).getExpression() != null) {
               this.method.getReturnType().defaultValue(this.method.getPosition()).compile(true, methodEnvironment);
               methodOutput.returnType(this.method.getReturnType().toASMType());
            }
         } else {
            this.method.getReturnType().defaultValue(this.method.getPosition()).compile(true, methodEnvironment);
            methodOutput.returnType(this.method.getReturnType().toASMType());
         }
      } else if (statements.length == 0 || !(statements[statements.length - 1] instanceof StatementReturn)) {
         methodOutput.ret();
      }

      methodOutput.end();
   }

   public class ZenClassMethod implements IJavaMethod {
      public ZenClassMethod() {
      }

      public boolean isStatic() {
         return false;
      }

      public boolean accepts(int numArguments) {
         int defaultArguments = ParsedZenClassMethod.this.method.countDefaultArguments();
         if (defaultArguments == 0) {
            return ParsedZenClassMethod.this.method.getArgumentTypes().length == numArguments;
         } else {
            return numArguments + defaultArguments >= ParsedZenClassMethod.this.method.getArgumentTypes().length;
         }
      }

      public boolean accepts(IEnvironmentGlobal environment, Expression... arguments) {
         return this.accepts(arguments.length) && IntStream.range(0, arguments.length).allMatch((i) -> {
            return arguments[i].getType().canCastImplicit(ParsedZenClassMethod.this.method.getArgumentTypes()[i], environment);
         });
      }

      public int getPriority(IEnvironmentGlobal environment, Expression... arguments) {
         return this.matchesExact(arguments) ? 3 : (this.accepts(environment, arguments) ? 1 : -1);
      }

      private boolean matchesExact(Expression... arguments) {
         if (!this.accepts(arguments.length)) {
            return false;
         } else {
            for(int i = 0; i < arguments.length; ++i) {
               if (arguments[i].getType().toJavaClass() != ParsedZenClassMethod.this.method.getArgumentTypes()[i].toJavaClass()) {
                  return false;
               }
            }

            return true;
         }
      }

      public void invokeVirtual(MethodOutput output) {
         output.invokeVirtual(ParsedZenClassMethod.this.className, ParsedZenClassMethod.this.method.getName(), ParsedZenClassMethod.this.method.getSignature());
      }

      public void invokeStatic(MethodOutput output) {
         throw new UnsupportedOperationException("Cannot statically invoke a virtual method");
      }

      public ZenType[] getParameterTypes() {
         return ParsedZenClassMethod.this.method.getArgumentTypes();
      }

      public ZenType getReturnType() {
         return ParsedZenClassMethod.this.method.getReturnType();
      }

      public boolean isVarargs() {
         return false;
      }

      public String getErrorDescription() {
         StringBuilder builder = (new StringBuilder(ParsedZenClassMethod.this.method.getName())).append("(");

         for(ZenType zenType : ParsedZenClassMethod.this.method.getArgumentTypes()) {
            builder.append(zenType.toString()).append(", ");
         }

         int length = builder.length();
         builder.delete(length - 2, length);
         return builder.append(")").toString();
      }

      public String getOwner() {
         return ParsedZenClassMethod.this.className;
      }

      public ParsedFunction getFunction() {
         return ParsedZenClassMethod.this.method;
      }
   }
}
