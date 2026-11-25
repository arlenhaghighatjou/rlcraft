package crafttweaker.mc1120.formatting;

import crafttweaker.api.formatting.IFormattedText;
import crafttweaker.api.formatting.IFormatter;
import net.minecraft.util.text.TextFormatting;

public class MCFormatter implements IFormatter {
   public MCFormatter() {
   }

   public IFormattedText string(String text) {
      return new FormattedString(text);
   }

   public IFormattedText black(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.BLACK, (IMCFormattedString)text);
   }

   public IFormattedText darkBlue(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.DARK_BLUE, (IMCFormattedString)text);
   }

   public IFormattedText darkGreen(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.DARK_GREEN, (IMCFormattedString)text);
   }

   public IFormattedText darkAqua(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.DARK_AQUA, (IMCFormattedString)text);
   }

   public IFormattedText darkRed(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.DARK_RED, (IMCFormattedString)text);
   }

   public IFormattedText darkPurple(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.DARK_PURPLE, (IMCFormattedString)text);
   }

   public IFormattedText gold(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.GOLD, (IMCFormattedString)text);
   }

   public IFormattedText gray(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.GRAY, (IMCFormattedString)text);
   }

   public IFormattedText darkGray(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.DARK_GRAY, (IMCFormattedString)text);
   }

   public IFormattedText blue(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.BLUE, (IMCFormattedString)text);
   }

   public IFormattedText green(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.GREEN, (IMCFormattedString)text);
   }

   public IFormattedText aqua(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.AQUA, (IMCFormattedString)text);
   }

   public IFormattedText red(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.RED, (IMCFormattedString)text);
   }

   public IFormattedText lightPurple(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.LIGHT_PURPLE, (IMCFormattedString)text);
   }

   public IFormattedText yellow(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.YELLOW, (IMCFormattedString)text);
   }

   public IFormattedText white(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.WHITE, (IMCFormattedString)text);
   }

   public IFormattedText obfuscated(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.OBFUSCATED, (IMCFormattedString)text);
   }

   public IFormattedText bold(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.BOLD, (IMCFormattedString)text);
   }

   public IFormattedText strikethrough(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.STRIKETHROUGH, (IMCFormattedString)text);
   }

   public IFormattedText underline(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.UNDERLINE, (IMCFormattedString)text);
   }

   public IFormattedText italic(IFormattedText text) {
      return new FormattedMarkupString(TextFormatting.ITALIC, (IMCFormattedString)text);
   }
}
