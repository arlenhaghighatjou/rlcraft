package stanhebben.zenscript.parser.expression;

import java.util.ArrayList;
import java.util.List;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.annotations.CompareType;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.ParsedFunctionArgument;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.ExpressionFloat;
import stanhebben.zenscript.expression.ExpressionInt;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.statements.Statement;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.type.ZenTypeAny;
import stanhebben.zenscript.type.ZenTypeDouble;
import stanhebben.zenscript.type.ZenTypeFloat;
import stanhebben.zenscript.type.ZenTypeInt;
import stanhebben.zenscript.util.StringUtil;
import stanhebben.zenscript.util.ZenPosition;

public abstract class ParsedExpression {
   private final ZenPosition position;

   public ParsedExpression(ZenPosition position) {
      this.position = position;
   }

   public static ParsedExpression read(ZenTokener parser, IEnvironmentGlobal environment) {
      return readAssignExpression(parser, environment);
   }

   private static ParsedExpression readAssignExpression(ZenTokener parser, IEnvironmentGlobal environment) {
      Token token = parser.peek();
      if (token == null) {
         ZenPosition position = new ZenPosition(parser.getFile(), parser.getLine(), parser.getLineOffset(), parser.getFile().getFileName());
         environment.error(position, "unexpected end of file; expression expected");
         return new ParsedExpressionInvalid(position);
      } else {
         ZenPosition position = token.getPosition();
         ParsedExpression left = readConditionalExpression(position, parser, environment);
         if (parser.peek() == null) {
            ZenPosition position2 = new ZenPosition(parser.getFile(), parser.getLine(), parser.getLineOffset(), parser.getFile().getFileName());
            environment.error(position2, "unexpected end of file - ; expected");
            return new ParsedExpressionInvalid(position2);
         } else {
            switch(parser.peek().getType()) {
            case 12:
               parser.next();
               return new ParsedExpressionOpAssign(position, left, readAssignExpression(parser, environment), OperatorType.ADD);
            case 13:
            case 15:
            case 17:
            case 19:
            case 21:
            case 23:
            case 25:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            default:
               return left;
            case 14:
               parser.next();
               return new ParsedExpressionOpAssign(position, left, readAssignExpression(parser, environment), OperatorType.SUB);
            case 16:
               parser.next();
               return new ParsedExpressionOpAssign(position, left, readAssignExpression(parser, environment), OperatorType.MUL);
            case 18:
               parser.next();
               return new ParsedExpressionOpAssign(position, left, readAssignExpression(parser, environment), OperatorType.DIV);
            case 20:
               parser.next();
               return new ParsedExpressionOpAssign(position, left, readAssignExpression(parser, environment), OperatorType.MOD);
            case 22:
               parser.next();
               return new ParsedExpressionOpAssign(position, left, readAssignExpression(parser, environment), OperatorType.OR);
            case 24:
               parser.next();
               return new ParsedExpressionOpAssign(position, left, readAssignExpression(parser, environment), OperatorType.AND);
            case 26:
               parser.next();
               return new ParsedExpressionOpAssign(position, left, readAssignExpression(parser, environment), OperatorType.XOR);
            case 39:
               parser.next();
               return new ParsedExpressionAssign(position, left, readAssignExpression(parser, environment));
            case 45:
               parser.next();
               return new ParsedExpressionOpAssign(position, left, readAssignExpression(parser, environment), OperatorType.CAT);
            }
         }
      }
   }

   private static ParsedExpression readConditionalExpression(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
      ParsedExpression left = readOrOrExpression(position, parser, environment);
      if (parser.optional(28) != null) {
         ParsedExpression onIf = readOrOrExpression(parser.peek().getPosition(), parser, environment);
         parser.required(29, ": expected");
         ParsedExpression onElse = readConditionalExpression(parser.peek().getPosition(), parser, environment);
         return new ParsedExpressionConditional(position, left, onIf, onElse);
      } else {
         return left;
      }
   }

   private static ParsedExpression readOrOrExpression(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
      ParsedExpression left;
      ParsedExpression right;
      for(left = readAndAndExpression(position, parser, environment); parser.optional(40) != null; left = new ParsedExpressionOrOr(position, left, right)) {
         right = readAndAndExpression(parser.peek().getPosition(), parser, environment);
      }

      return left;
   }

   private static ParsedExpression readAndAndExpression(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
      ParsedExpression left;
      ParsedExpression right;
      for(left = readOrExpression(position, parser, environment); parser.optional(41) != null; left = new ParsedExpressionAndAnd(position, left, right)) {
         right = readOrExpression(parser.peek().getPosition(), parser, environment);
      }

      return left;
   }

   private static ParsedExpression readOrExpression(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
      ParsedExpression left;
      ParsedExpression right;
      for(left = readXorExpression(position, parser, environment); parser.optional(23) != null; left = new ParsedExpressionBinary(position, left, right, OperatorType.OR)) {
         right = readXorExpression(parser.peek().getPosition(), parser, environment);
      }

      return left;
   }

   private static ParsedExpression readXorExpression(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
      ParsedExpression left;
      ParsedExpression right;
      for(left = readAndExpression(position, parser, environment); parser.optional(27) != null; left = new ParsedExpressionBinary(position, left, right, OperatorType.XOR)) {
         right = readAndExpression(parser.peek().getPosition(), parser, environment);
      }

      return left;
   }

   private static ParsedExpression readAndExpression(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
      ParsedExpression left;
      ParsedExpression right;
      for(left = readCompareExpression(position, parser, environment); parser.optional(25) != null; left = new ParsedExpressionBinary(position, left, right, OperatorType.AND)) {
         right = readCompareExpression(parser.peek().getPosition(), parser, environment);
      }

      return left;
   }

   private static ParsedExpression readCompareExpression(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
      ParsedExpression left = readAddExpression(position, parser, environment);
      switch(parser.peek() == null ? -1 : parser.peek().getType()) {
      case 34:
         parser.next();
         ParsedExpression right = readAddExpression(parser.peek().getPosition(), parser, environment);
         return new ParsedExpressionCompare(position, left, right, CompareType.LE);
      case 35:
         parser.next();
         ParsedExpression right = readAddExpression(parser.peek().getPosition(), parser, environment);
         return new ParsedExpressionCompare(position, left, right, CompareType.LT);
      case 36:
         parser.next();
         ParsedExpression right = readAddExpression(parser.peek().getPosition(), parser, environment);
         return new ParsedExpressionCompare(position, left, right, CompareType.GE);
      case 37:
         parser.next();
         ParsedExpression right = readAddExpression(parser.peek().getPosition(), parser, environment);
         return new ParsedExpressionCompare(position, left, right, CompareType.GT);
      case 38:
         parser.next();
         ParsedExpression right = readAddExpression(parser.peek().getPosition(), parser, environment);
         return new ParsedExpressionCompare(position, left, right, CompareType.EQ);
      case 42:
         parser.next();
         ParsedExpression right = readAddExpression(parser.peek().getPosition(), parser, environment);
         return new ParsedExpressionCompare(position, left, right, CompareType.NE);
      case 109:
         parser.next();
         ParsedExpression right = readAddExpression(parser.peek().getPosition(), parser, environment);
         return new ParsedExpressionBinary(position, left, right, OperatorType.CONTAINS);
      default:
         return left;
      }
   }

   private static ParsedExpression readAddExpression(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
      ParsedExpression left = readMulExpression(position, parser, environment);

      while(true) {
         while(parser.optional(13) == null) {
            if (parser.optional(15) != null) {
               ParsedExpression right = readMulExpression(parser.peek().getPosition(), parser, environment);
               left = new ParsedExpressionBinary(position, left, right, OperatorType.SUB);
            } else {
               if (parser.optional(32) == null) {
                  return left;
               }

               ParsedExpression right = readMulExpression(parser.peek().getPosition(), parser, environment);
               left = new ParsedExpressionBinary(position, left, right, OperatorType.CAT);
            }
         }

         ParsedExpression right = readMulExpression(parser.peek().getPosition(), parser, environment);
         left = new ParsedExpressionBinary(position, left, right, OperatorType.ADD);
      }
   }

   private static ParsedExpression readMulExpression(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
      ParsedExpression left = readUnaryExpression(position, parser, environment);

      while(true) {
         while(parser.optional(17) == null) {
            if (parser.optional(19) != null) {
               ParsedExpression right = readUnaryExpression(parser.peek().getPosition(), parser, environment);
               left = new ParsedExpressionBinary(position, left, right, OperatorType.DIV);
            } else {
               if (parser.optional(21) == null) {
                  return left;
               }

               ParsedExpression right = readUnaryExpression(parser.peek().getPosition(), parser, environment);
               left = new ParsedExpressionBinary(position, left, right, OperatorType.MOD);
            }
         }

         ParsedExpression right = readUnaryExpression(parser.peek().getPosition(), parser, environment);
         left = new ParsedExpressionBinary(position, left, right, OperatorType.MUL);
      }
   }

   private static ParsedExpression readUnaryExpression(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
      switch(parser.peek().getType()) {
      case 15:
         parser.next();
         return new ParsedExpressionUnary(position, readUnaryExpression(parser.peek().getPosition(), parser, environment), OperatorType.NEG);
      case 43:
         parser.next();
         return new ParsedExpressionUnary(position, readUnaryExpression(parser.peek().getPosition(), parser, environment), OperatorType.NOT);
      default:
         return readPostfixExpression(position, parser, environment);
      }
   }

   private static ParsedExpression readPostfixExpression(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
      ParsedExpression base = readPrimaryExpression(position, parser, environment);

      while(true) {
         Token next = parser.peek();
         if (next != null) {
            if (parser.optional(10) != null) {
               Token indexString = parser.optional(new int[]{1, 121, 107});
               if (indexString != null) {
                  base = new ParsedExpressionMember(position, base, indexString.getValue());
               } else {
                  Token indexString2 = parser.optional(4);
                  if (indexString2 == null) {
                     Token last = parser.next();
                     throw new ParseException(last, "Invalid expression, last token: " + last.getValue());
                  }

                  base = new ParsedExpressionMember(position, base, StringUtil.unescapeString(indexString2.getValue()));
               }
               continue;
            }

            if (next.getType() == 9 || next.getType() == 1 && next.getValue().equals("to")) {
               parser.next();
               ParsedExpression to = readAssignExpression(parser, environment);
               return new ParsedExpressionBinary(position, base, to, OperatorType.RANGE);
            }

            if (parser.optional(7) != null) {
               ParsedExpression index = readAssignExpression(parser, environment);
               parser.required(8, "] expected");
               if (parser.optional(39) != null) {
                  ParsedExpression newVal = readAssignExpression(parser, environment);
                  base = new ParsedExpressionIndexSet(position, base, index, newVal);
               } else {
                  base = new ParsedExpressionIndex(position, base, index);
               }
               continue;
            }

            if (parser.optional(30) != null) {
               ArrayList<ParsedExpression> arguments = new ArrayList();
               if (parser.optional(31) == null) {
                  arguments.add(readAssignExpression(parser, environment));

                  while(parser.optional(11) != null) {
                     arguments.add(readAssignExpression(parser, environment));
                  }

                  parser.required(31, ") expected");
               }

               base = new ParsedExpressionCall(position, base, arguments);
               continue;
            }

            if (parser.optional(120) != null) {
               ZenType type = ZenType.read(parser, environment);
               base = new ParsedExpressionCast(position, base, type);
               continue;
            }

            if (parser.optional(668) != null) {
               ZenType type = ZenType.read(parser, environment);
               base = new ParsedExpressionInstanceOf(position, base, type);
               continue;
            }
         }

         return base;
      }
   }

   private static ParsedExpression readPrimaryExpression(ZenPosition position, ZenTokener parser, IEnvironmentGlobal environment) {
      switch(parser.peek().getType()) {
      case 1:
         return new ParsedExpressionVariable(position, parser.next().getValue());
      case 2:
         long l = Long.decode(parser.next().getValue()).longValue();
         return new ParsedExpressionValue(position, new ExpressionInt(position, l, (ZenType)(Long.bitCount(l) > 32 ? ZenType.LONG : ZenTypeInt.INSTANCE)));
      case 3:
         String value = parser.next().getValue();
         ZenType zenType = ZenTypeDouble.INSTANCE;
         if (value.endsWith("f") || value.endsWith("F")) {
            zenType = ZenTypeFloat.INSTANCE;
            value = value.substring(0, value.length() - 1);
         }

         return new ParsedExpressionValue(position, new ExpressionFloat(position, Double.parseDouble(value), zenType));
      case 4:
         return new ParsedExpressionValue(position, new ExpressionString(position, StringUtil.unescapeString(parser.next().getValue())));
      case 5:
         parser.next();
         List<ParsedExpression> keys = new ArrayList();
         List<ParsedExpression> values = new ArrayList();
         if (parser.optional(6) == null) {
            while(parser.optional(6) == null) {
               keys.add(readAssignExpression(parser, environment));
               parser.required(29, ": expected");
               values.add(readAssignExpression(parser, environment));
               if (parser.optional(11) == null) {
                  parser.required(6, "} or , expected");
                  break;
               }
            }
         }

         return new ParsedExpressionMap(position, keys, values);
      case 7:
         parser.next();
         List<ParsedExpression> contents = new ArrayList();
         if (parser.optional(8) == null) {
            while(parser.optional(8) == null) {
               contents.add(readAssignExpression(parser, environment));
               if (parser.optional(11) == null) {
                  parser.required(8, "] or , expected");
                  break;
               }
            }
         }

         return new ParsedExpressionArray(position, contents);
      case 30:
         parser.next();
         ParsedExpression result = readAssignExpression(parser, environment);
         parser.required(31, ") expected");
         return result;
      case 35:
         Token start = parser.next();
         List<Token> tokens = new ArrayList();

         for(Token next = parser.next(); next.getType() != 37; next = parser.next()) {
            tokens.add(next);
         }

         IZenSymbol resolved = parser.getEnvironment().getBracketed(environment, tokens);
         if (resolved == null) {
            StringBuilder builder = new StringBuilder();
            builder.append('<');
            Token last = null;

            for(Token token : tokens) {
               if (last != null) {
                  builder.append(' ');
               }

               builder.append(token.getValue());
               last = token;
            }

            builder.append('>');
            if (!parser.ignoreBracketErrors) {
               parser.getEnvironment().getErrorLogger().error(start.getPosition(), "Could not resolve " + builder.toString());
            } else {
               parser.getEnvironment().getErrorLogger().info(start.getPosition(), "Could not resolve " + builder.toString());
            }

            return new ParsedExpressionInvalid(start.getPosition());
         }

         return new ParsedExpressionValue(start.getPosition(), parser.getEnvironment().getBracketed(environment, tokens).instance(start.getPosition()));
      case 108:
         parser.next();
         parser.required(30, "( expected");
         List<ParsedFunctionArgument> arguments = new ArrayList();
         if (parser.optional(31) == null) {
            while(true) {
               String name = parser.required(1, "identifier expected").getValue();
               ZenType type = ZenTypeAny.INSTANCE;
               ParsedExpression defaultExpression = null;
               if (parser.optional(120) != null) {
                  type = ZenType.read(parser, environment);
               }

               if (parser.optional(39) != null) {
                  defaultExpression = read(parser, environment);
               }

               arguments.add(new ParsedFunctionArgument(name, type, defaultExpression));
               if (parser.optional(11) == null) {
                  break;
               }
            }

            parser.required(31, ") expected");
         }

         ZenType returnType = ZenTypeAny.INSTANCE;
         if (parser.optional(120) != null) {
            returnType = ZenType.read(parser, environment);
         }

         parser.required(5, "{ expected");
         List<Statement> statements = new ArrayList();
         if (parser.optional(6) == null) {
            while(parser.optional(6) == null) {
               statements.add(Statement.read(parser, environment, returnType));
            }
         }

         return new ParsedExpressionFunction(position, returnType, arguments, statements);
      case 140:
         parser.next();
         return new ParsedExpressionNull(position);
      case 141:
         parser.next();
         return new ParsedExpressionBool(position, true);
      case 142:
         parser.next();
         return new ParsedExpressionBool(position, false);
      default:
         Token last = parser.next();
         throw new ParseException(last, "Invalid expression, last token: " + last.getValue());
      }
   }

   public ZenPosition getPosition() {
      return this.position;
   }

   public abstract IPartialExpression compile(IEnvironmentMethod var1, ZenType var2);

   public Expression compileKey(IEnvironmentMethod environment, ZenType predictedType) {
      return this.compile(environment, predictedType).eval(environment);
   }
}
