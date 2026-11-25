package stanhebben.zenscript.parser;

import stanhebben.zenscript.ZenParsedFile;

public class TokenException extends RuntimeException {
   public TokenException(ZenParsedFile file, int line, int lineOffset, char value) {
      super("Invalid character at " + file + ":" + line + " - " + value);
      if (line < 0) {
         throw new IllegalArgumentException("Line cannot be negative");
      }
   }
}
