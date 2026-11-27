package snownee.kiwi.util;

import net.lax1dude.eaglercraft.v1_8.HString;

import java.text.DecimalFormat;
import java.text.MessageFormat;

public class Util {
   private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###");
   public static final MessageFormat MESSAGE_FORMAT = new MessageFormat("{0,number,#.#}");

   public Util() {
   }

   public static String color(int color) {
      return HString.format("\u00a7x%06x", color & 16777215);
   }

   public static String formatComma(long number) {
      return DECIMAL_FORMAT.format(number);
   }

   public static String formatCompact(long number) {
      int unit = 1000;
      if (number < (long)unit) {
         return Long.toString(number);
      } else {
         int exp = (int)(Math.log((double)number) / Math.log((double)unit));
         if (exp - 1 >= 0 && exp - 1 < 6) {
            char pre = "kMGTPE".charAt(exp - 1);
            return MESSAGE_FORMAT.format(new Double[]{(double)number / Math.pow((double)unit, (double)exp)}) + pre;
         } else {
            return Long.toString(number);
         }
      }
   }
}
