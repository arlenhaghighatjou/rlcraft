package stanhebben.zenscript.definitions;

import java.util.ArrayList;
import java.util.List;
import stanhebben.zenscript.ZenParsedFile;
import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialExpansionCall;
import stanhebben.zenscript.parser.ParseException;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedExpansion {
   private final ParsedFunction function;
   private final ZenType type;
   private final String owner;

   public ParsedExpansion(ParsedFunction function, ZenType type, ZenParsedFile owner) {
      this.function = function;
      this.type = type;
      this.owner = owner.getClassName();
   }

   public static ParsedExpansion parse(ZenTokener parser, IEnvironmentGlobal environment, ZenParsedFile owner) {
      parser.next();
      Token expandKeyword = parser.next();
      if (expandKeyword.getType() == 1 && expandKeyword.getValue().equals("expand")) {
         ZenType type = ZenType.read(parser, environment);
         Token token = parser.peek();
         if (token.getType() != 44) {
            throw new ParseException(token, "$ expected");
         } else {
            List<ParsedFunctionArgument> arguments = new ArrayList();
            arguments.add(new ParsedFunctionArgument("this", type, (ParsedExpression)null));
            ParsedFunction function = ParsedFunction.parse(parser, environment, arguments);
            return new ParsedExpansion(function, type, owner);
         }
      } else {
         throw new ParseException(expandKeyword, "expand expected");
      }
   }

   public String getName() {
      return this.function.getName();
   }

   public String getCompileName() {
      return "expand$" + this.type.getNameForInterfaceSignature() + "$" + this.getName();
   }

   public ParsedFunction getFunction() {
      return this.function;
   }

   public ZenType getType() {
      return this.type;
   }

   public String getOwner() {
      return this.owner;
   }

   public IPartialExpression instance(ZenPosition position, Expression value) {
      return new PartialExpansionCall(position, this.owner, this.getCompileName(), this.function.getSignature(), this.function.getArguments(), this.function.getReturnType(), value);
   }
}
