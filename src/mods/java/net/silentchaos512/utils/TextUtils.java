package net.silentchaos512.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class TextUtils {
   public TextUtils() {
   }

   public static String lower(String str) {
      return str.toLowerCase(Locale.ROOT);
   }

   public static List<String> wrapLines(Iterable<String> list, int lineWidth, Function<String, String> stripper) {
      List<String> result = new ArrayList();

      for(String str : list) {
         String format = (String)stripper.apply(str);
         String[] split = new String[0];

         while(str.length() > lineWidth && split.length != 1) {
            split = splitLine(str, lineWidth);
            result.add(format + split[0]);
            if (split.length > 1) {
               str = split[1];
            }
         }

         if (split.length != 1) {
            result.add(format + str);
         }
      }

      return result;
   }

   private static String[] splitLine(String str, int lineWidth) {
      if (str.length() <= lineWidth) {
         return new String[]{str};
      } else {
         for(int c = lineWidth; c < str.length(); ++c) {
            if (Character.isWhitespace(str.charAt(c))) {
               return new String[]{str.substring(0, c), str.substring(c + 1)};
            }
         }

         return new String[]{str};
      }
   }
}
