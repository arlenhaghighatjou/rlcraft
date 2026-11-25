package lykrast.defiledlands.common.util;

import com.google.common.collect.Lists;
import java.util.List;

public class LocUtils {
   public LocUtils() {
   }

   public static List<String> getTooltips(String text) {
      List<String> list = Lists.newLinkedList();
      if (text != null && !text.isEmpty()) {
         int j;
         int k;
         for(j = 0; (k = text.indexOf("\\n", j)) >= 0; j = k + 2) {
            list.add(text.substring(j, k));
         }

         list.add(text.substring(j, text.length()));
         return list;
      } else {
         return list;
      }
   }

   public static String convertNewlines(String line) {
      if (line == null) {
         return null;
      } else {
         int j;
         while((j = line.indexOf("\\n")) >= 0) {
            line = line.substring(0, j) + '\n' + line.substring(j + 2);
         }

         return line;
      }
   }

   public static String prefix(String s) {
      return "defiledlands." + s;
   }
}
