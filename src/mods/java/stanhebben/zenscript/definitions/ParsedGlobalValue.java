package stanhebben.zenscript.definitions;

import stanhebben.zenscript.ZenTokener;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.parser.expression.ParsedExpression;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class ParsedGlobalValue {
   private final ZenPosition position;
   private final String name;
   private final ZenType type;
   private final ParsedExpression value;
   private final String owner;
   private final boolean global;

   ParsedGlobalValue(ZenPosition position, String name, ZenType type, ParsedExpression value, String owner, boolean global) {
      this.position = position;
      this.name = name;
      this.type = type;
      this.value = value;
      this.owner = owner;
      this.global = global;
   }

   public String getName() {
      return this.name;
   }

   public ZenPosition getPosition() {
      return this.position;
   }

   public ZenType getType() {
      return this.type;
   }

   public ParsedExpression getValue() {
      return this.value;
   }

   public String getOwner() {
      return this.owner;
   }

   public static ParsedGlobalValue parse(ZenTokener parser, IEnvironmentGlobal environment, String owner, boolean global) {
      Token startingPoint = parser.next();
      String name = parser.required(1, "Global value requires a name!").getValue();
      ZenType type = ZenType.ANY;
      Token nee = parser.optional(120);
      if (nee != null) {
         type = ZenType.read(parser, environment);
      }

      parser.required(39, "Global values have to be initialized!");
      ParsedExpression value = ParsedExpression.read(parser, environment);
      parser.required(33, "; expected");
      return new ParsedGlobalValue(startingPoint.getPosition(), name, type, value, owner, global);
   }

   public boolean isGlobal() {
      return this.global;
   }
}
