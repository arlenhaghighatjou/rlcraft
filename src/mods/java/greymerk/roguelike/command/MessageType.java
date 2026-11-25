package greymerk.roguelike.command;

import greymerk.roguelike.util.TextFormat;

public enum MessageType {
   INFO(TextFormat.GRAY),
   ERROR(TextFormat.RED),
   SUCCESS(TextFormat.GREEN),
   SPECIAL(TextFormat.GOLD),
   WARNING(TextFormat.YELLOW);

   private TextFormat textFormat;

   private MessageType(TextFormat textFormat) {
      this.textFormat = textFormat;
   }

   public TextFormat getTextFormat() {
      return this.textFormat;
   }

   public String apply(String message) {
      return this.getTextFormat().apply(message);
   }
}
