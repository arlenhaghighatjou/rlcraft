package greymerk.roguelike.util;

import net.lax1dude.eaglercraft.v1_8.HString;

public enum TextFormat {
   BLACK('0'),
   DARKBLUE('1'),
   DARKGREEN('2'),
   DARKAQUA('3'),
   DARKRED('4'),
   DARKPURPLE('5'),
   GOLD('6'),
   GRAY('7'),
   DARKGRAY('8'),
   BLUE('9'),
   GREEN('a'),
   AQUA('b'),
   RED('c'),
   LIGHTPURPLE('d'),
   YELLOW('e'),
   WHITE('f'),
   OBFUSCATED('k'),
   BOLD('l'),
   STRIKETHROUGH('m'),
   UNDERLINE('n'),
   ITALIC('o'),
   RESET('r');

   private char codeChar;

   private TextFormat(char codeChar) {
      this.codeChar = codeChar;
   }

   public char getCodeChar() {
      return this.codeChar;
   }

   public String apply(String text) {
      return HString.format("\u00c2\u00a7%s%s", this.getCodeChar(), text);
   }
}
