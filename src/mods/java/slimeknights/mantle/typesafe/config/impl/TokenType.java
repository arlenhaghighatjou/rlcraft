package slimeknights.mantle.typesafe.config.impl;

enum TokenType {
   START,
   END,
   COMMA,
   EQUALS,
   COLON,
   OPEN_CURLY,
   CLOSE_CURLY,
   OPEN_SQUARE,
   CLOSE_SQUARE,
   VALUE,
   NEWLINE,
   UNQUOTED_TEXT,
   IGNORED_WHITESPACE,
   SUBSTITUTION,
   PROBLEM,
   COMMENT,
   PLUS_EQUALS;

   private TokenType() {
   }
}
