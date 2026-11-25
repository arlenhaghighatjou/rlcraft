package stanhebben.zenscript.parser;

import stanhebben.zenscript.util.ZenPosition;

public class Token {
   private final ZenPosition position;
   private final String value;
   private final int type;

   public Token(String value, int type, ZenPosition position) {
      this.value = value;
      this.type = type;
      this.position = position;
   }

   public ZenPosition getPosition() {
      return this.position;
   }

   public String getValue() {
      return this.value;
   }

   public int getType() {
      return this.type;
   }

   public String toString() {
      return this.position.getLine() + ":" + this.position.getLineOffset() + " (" + this.type + ") " + this.value;
   }
}
