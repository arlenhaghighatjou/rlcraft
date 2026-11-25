package stanhebben.zenscript.statements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public abstract class Statement {
   private final ZenPosition position;

   public Statement(ZenPosition position) {
      this.position = position;
   }

   public static Statement read(ZenTokener parser, IEnvironmentGlobal environment, ZenType returnType) {
      Token next = parser.peek();
      switch(next.getType()) {
      case 5:
         Token t = parser.next();
         ArrayList<Statement> statements = new ArrayList();

         while(parser.optional(6) == null) {
            statements.add(read(parser, environment, returnType));
         }

         return new StatementBlock(t.getPosition(), statements);
      case 121:
         Token t = parser.next();
         parser.required(2, "integer expected");
         parser.required(33, "; expected");
         return new StatementNull(t.getPosition());
      case 122:
         Token t = parser.next();
         ParsedExpression expression = ParsedExpression.read(parser, environment);
         Statement onIf = read(parser, environment, returnType);
         Statement onElse = null;
         if (parser.optional(123) != null) {
            onElse = read(parser, environment, returnType);
         }

         return new StatementIf(t.getPosition(), expression, onIf, onElse);
      case 124:
         Token t = parser.next();
         String name = parser.required(1, "identifier expected").getValue();
         List<String> names = new ArrayList();
         names.add(name);

         while(parser.optional(11) != null) {
            names.add(parser.required(1, "identifier expected").getValue());
         }

         parser.required(109, "in expected");
         ParsedExpression source = ParsedExpression.read(parser, environment);
         Statement content = read(parser, environment, (ZenType)null);
         return new StatementForeach(t.getPosition(), (String[])names.toArray(new String[names.size()]), source, content);
      case 125:
         parser.next();
         ParsedExpression expression = null;
         if (parser.peek() != null && !parser.isNext(33)) {
            expression = ParsedExpression.read(parser, environment);
         }

         parser.required(33, "; expected");
         return new StatementReturn(next.getPosition(), returnType, expression);
      case 126:
      case 127:
         Token start = parser.next();
         String name = parser.required(1, "identifier expected").getValue();
         ZenType type = null;
         ParsedExpression initializer = null;
         if (parser.optional(120) != null) {
            type = ZenType.read(parser, environment);
         }

         if (parser.optional(39) != null) {
            initializer = ParsedExpression.read(parser, environment);
         }

         parser.required(33, "; expected");
         return new StatementVar(start.getPosition(), name, type, initializer, start.getType() == 127);
      case 128:
         parser.next();
         ParsedExpression condition = ParsedExpression.read(parser, environment);
         Statement content = read(parser, environment, (ZenType)null);
         return new StatementWhileDo(next.getPosition(), content, condition);
      case 129:
         parser.next();
         parser.required(33, "; expected");
         return new StatementBreak(next.getPosition());
      case 130:
         parser.next();
         parser.required(33, "; expected");
         return new StatementContinue(next.getPosition());
      default:
         ZenPosition position = parser.peek().getPosition();
         StatementExpression result = new StatementExpression(position, ParsedExpression.read(parser, environment));
         parser.required(33, "; expected");
         return result;
      }
   }

   public ZenPosition getPosition() {
      return this.position;
   }

   public boolean isReturn() {
      return false;
   }

   public abstract void compile(IEnvironmentMethod var1);

   public List<Statement> getSubStatements() {
      return Collections.singletonList(this);
   }

   public void compile(IEnvironmentMethod environment, boolean forced) {
      environment.getOutput().position(this.getPosition());
      this.compile(environment);
   }
}
