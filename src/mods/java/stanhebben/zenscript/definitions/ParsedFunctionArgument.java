package stanhebben.zenscript.definitions;

import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;

public class ParsedFunctionArgument {
   private final String name;
   private final ZenType type;
   private final ParsedExpression defaultExpression;

   public ParsedFunctionArgument(String name, ZenType type, ParsedExpression defaultExpression) {
      this.name = name;
      this.type = type;
      this.defaultExpression = defaultExpression;
   }

   /** @deprecated */
   @Deprecated
   public ParsedFunctionArgument(String name, ZenType type) {
      this(name, type, (ParsedExpression)null);
   }

   public String getName() {
      return this.name;
   }

   public ZenType getType() {
      return this.type;
   }

   public ParsedExpression getDefaultExpression() {
      return this.defaultExpression;
   }
}
