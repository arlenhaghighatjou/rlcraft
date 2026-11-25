package reborncore.common.util;

import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class StringUtils {
   public StringUtils() {
   }

   public static String toFirstCapital(String input) {
      return input != null && input.length() != 0 ? input.substring(0, 1).toUpperCase() + input.substring(1) : input;
   }

   public static String toFirstCapitalAllLowercase(String input) {
      if (input != null && input.length() != 0) {
         String output = input.toLowerCase();
         return output.substring(0, 1).toUpperCase() + output.substring(1);
      } else {
         return input;
      }
   }

   public static String t(String translationKey) {
      return (new TextComponentTranslation(translationKey, new Object[0])).getFormattedText();
   }

   public static String t(String translationKey, Object... format) {
      return (new TextComponentTranslation(translationKey, format)).getFormattedText();
   }

   public static TextFormatting getPercentageColour(int percentage) {
      if (percentage <= 10) {
         return TextFormatting.RED;
      } else {
         return percentage >= 75 ? TextFormatting.GREEN : TextFormatting.YELLOW;
      }
   }
}
