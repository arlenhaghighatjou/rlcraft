package ivorius.ivtoolkit.tools;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class IvStringHelper {
   public IvStringHelper() {
   }

   public static String cheeseString(String string, float effect, long seed) {
      return cheeseString(string, effect, new EaglercraftRandom(seed));
   }

   public static String cheeseString(String string, float effect, EaglercraftRandom rand) {
      if (effect <= 0.0F) {
         return string;
      } else {
         StringBuilder builder = new StringBuilder(string.length());

         for(int i = 0; i < string.length(); ++i) {
            if (rand.nextFloat() <= effect) {
               builder.append(' ');
            } else {
               builder.append(string.charAt(i));
            }
         }

         return builder.toString();
      }
   }

   public static int countOccurrences(String haystack, char needle) {
      int count = 0;

      for(int i = 0; i < haystack.length(); ++i) {
         if (haystack.charAt(i) == needle) {
            ++count;
         }
      }

      return count;
   }
}
