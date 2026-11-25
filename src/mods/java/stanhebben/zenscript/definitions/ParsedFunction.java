package stanhebben.zenscript.definitions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.parser.expression.ParsedExpressionVariable;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAny;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedFunction {
   private final ZenPosition position;
   private final String name;
   private final List<ParsedFunctionArgument> arguments;
   private final ZenType returnType;
   private final Statement[] statements;
   private final String signature;

   public ParsedFunction(ZenPosition position, String name, List<ParsedFunctionArgument> arguments, ZenType returnType, Statement[] statements) {
      this.position = position;
      this.name = name;
      this.arguments = arguments;
      this.returnType = returnType;
      this.statements = statements;
      StringBuilder sig = new StringBuilder();
      sig.append("(");

      for(ParsedFunctionArgument argument : arguments) {
         sig.append(argument.getType().getSignature());
      }

      sig.append(")");
      sig.append(returnType.getSignature());
      this.signature = sig.toString();
   }

   public static ParsedFunction parse(ZenTokener parser, IEnvironmentGlobal environment) {
      return parse(parser, environment, new ArrayList());
   }

   static ParsedFunction parse(ZenTokener parser, IEnvironmentGlobal environment, List<ParsedFunctionArgument> arguments) {
      parser.next();
      Token tName = parser.required(1, "identifier expected");
      parser.required(30, "( expected");
      if (parser.optional(31) == null) {
         Token argName = parser.required(1, "identifier expected");
         ZenType type = ZenTypeAny.INSTANCE;
         ParsedExpression expression = null;
         boolean hasDefaultArgument = false;
         if (parser.optional(120) != null) {
            type = ZenType.read(parser, environment);
         }

         if (parser.optional(39) != null) {
            expression = ParsedExpression.read(parser, environment);
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
               type2 = ZenType.read(parser, environment);
            }

            if (parser.optional(39) != null) {
               expression2 = ParsedExpression.read(parser, environment);
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
         type = ZenType.read(parser, environment);
      }

      parser.required(5, "{ expected");
      Statement[] statements;
      if (parser.optional(6) != null) {
         statements = new Statement[0];
      } else {
         ArrayList<Statement> statementsAL = new ArrayList();

         while(parser.optional(6) == null) {
            statementsAL.add(Statement.read(parser, environment, type));
         }

         statements = (Statement[])statementsAL.toArray(new Statement[statementsAL.size()]);
      }

      return new ParsedFunction(tName.getPosition(), tName.getValue(), arguments, type, statements);
   }

   public ZenPosition getPosition() {
      return this.position;
   }

   public String getName() {
      return this.name;
   }

   public String getSignature() {
      return this.signature;
   }

   public ZenType getReturnType() {
      return this.returnType;
   }

   public List<ParsedFunctionArgument> getArguments() {
      return this.arguments;
   }

   public int countDefaultArguments() {
      return (int)this.arguments.stream().map(ParsedFunctionArgument::getDefaultExpression).filter(Objects::nonNull).count();
   }

   public ZenType[] getArgumentTypes() {
      ZenType[] result = new ZenType[this.arguments.size()];

      for(int i = 0; i < this.arguments.size(); ++i) {
         result[i] = ((ParsedFunctionArgument)this.arguments.get(i)).getType();
      }

      return result;
   }

   public String getDefaultParameterFieldName(int number) {
      StringBuilder builder = new StringBuilder("method_default_parameter_");
      builder.append(this.name);

      for(ZenType argumentType : this.getArgumentTypes()) {
         builder.append(argumentType.toASMType().getDescriptor());
      }

      builder.append("_").append(number);
      String name = builder.toString();
      name = name.replace("[", "array_").replace("/", "_").replace(";", "");
      return name;
   }

   public Statement[] getStatements() {
      return this.statements;
   }
}
