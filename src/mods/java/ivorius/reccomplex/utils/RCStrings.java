package ivorius.reccomplex.utils;

import javax.annotation.Nonnull;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class RCStrings {
   public RCStrings() {
   }

   private static MutablePair<Integer, Integer> abbreviate(String str, int offset, int maxWidth) {
      if (str == null) {
         return null;
      } else if (maxWidth < 4) {
         throw new IllegalArgumentException("Minimum abbreviation width is 4");
      } else if (str.length() <= maxWidth) {
         return MutablePair.of(Integer.valueOf(0), Integer.valueOf(str.length()));
      } else {
         if (offset > str.length()) {
            offset = str.length();
         }

         if (str.length() - offset < maxWidth - 3) {
            offset = str.length() - (maxWidth - 3);
         }

         if (offset <= 4) {
            return MutablePair.of(Integer.valueOf(0), Integer.valueOf(maxWidth - 3));
         } else if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
         } else if (offset + maxWidth - 3 < str.length()) {
            Pair<Integer, Integer> abbreviated = abbreviate(str.substring(offset), 0, maxWidth - 3);
            return MutablePair.of(Integer.valueOf(offset), Integer.valueOf(offset + ((Integer)abbreviated.getRight()).intValue()));
         } else {
            return MutablePair.of(Integer.valueOf(str.length() - (maxWidth - 3)), Integer.valueOf(str.length()));
         }
      }
   }

   @Nonnull
   public static String abbreviateFormatted(String str, int offset, int maxWidth) {
      MutablePair<Integer, Integer> abbreviate = abbreviate(str, offset, maxWidth);
      String abbreviated = str.substring(((Integer)abbreviate.getLeft()).intValue(), ((Integer)abbreviate.getRight()).intValue());
      if (((Integer)abbreviate.getLeft()).intValue() > 0) {
         String prevFormat = "";
         if (str.length() > ((Integer)abbreviate.getLeft()).intValue()) {
            for(int i = ((Integer)abbreviate.getLeft()).intValue() - 1; i >= 0; --i) {
               char fst = str.charAt(i);
               char scd = str.charAt(i + 1);
               if (fst == 167 && scd != 167) {
                  prevFormat = "" + fst + scd + prevFormat;
               }
            }
         }

         if (str.charAt(((Integer)abbreviate.getLeft()).intValue() - 1) == 167) {
            abbreviated = abbreviated.substring(1);
         }

         abbreviated = "..." + prevFormat + abbreviated;
      }

      if (((Integer)abbreviate.getRight()).intValue() < str.length()) {
         if (str.charAt(((Integer)abbreviate.getRight()).intValue() - 1) == 167) {
            abbreviated = abbreviated.substring(0, abbreviated.length() - 1);
         }

         abbreviated = abbreviated + TextFormatting.RESET + "...";
      }

      return abbreviated;
   }

   public static String abbreviateFormatted(String str, int maxWidth) {
      return abbreviateFormatted(str, 0, maxWidth);
   }

   public static Long seed(String seed) {
      if (seed != null && !StringUtils.isEmpty(seed)) {
         try {
            long j = Long.parseLong(seed);
            return j != 0L ? j : null;
         } catch (NumberFormatException var3) {
            return (long)seed.hashCode();
         }
      } else {
         return null;
      }
   }

   public static String shorten(String string, FontRenderer renderer, int width) {
      boolean shortened;
      for(shortened = false; renderer.getStringWidth(string) > width; string = string.substring(0, string.length() - 1)) {
         if (!shortened) {
            shortened = true;
            string = "..." + string;
         }
      }

      return shortened ? string.substring(3) + TextFormatting.RESET + "..." : string;
   }
}
