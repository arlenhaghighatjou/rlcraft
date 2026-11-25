package stanhebben.zenscript.parser;

import stanhebben.zenscript.ZenParsedFile;

public class ParseException extends RuntimeException {
   private final ZenParsedFile file;
   private final int line;
   private final int lineOffset;
   private final Token token;
   private final String message;

   public ParseException(Token token, String error) {
      super("Error parsing line " + token.getPosition().getLine() + ":" + token.getPosition().getLineOffset() + " - " + error + " (last token: " + token.getValue() + ")");
      this.file = token.getPosition().getFile();
      this.line = token.getPosition().getLine();
      this.lineOffset = token.getPosition().getLineOffset();
      this.token = token;
      this.message = error;
   }

   public ParseException(ZenParsedFile file, int line, int lineOffset, String error) {
      super("Error parsing line " + line + ":" + lineOffset + " - " + error);
      this.file = file;
      this.line = line;
      this.lineOffset = lineOffset;
      this.token = null;
      this.message = error;
   }

   public ZenParsedFile getFile() {
      return this.file;
   }

   public int getLine() {
      return this.line;
   }

   public int getLineOffset() {
      return this.lineOffset;
   }

   public String getExplanation() {
      return this.message;
   }
}
