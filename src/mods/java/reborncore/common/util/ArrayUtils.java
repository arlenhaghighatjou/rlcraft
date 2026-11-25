package reborncore.common.util;

import java.util.Collection;

public class ArrayUtils {
   public ArrayUtils() {
   }

   public static String[] arrayToLowercase(String[] array) {
      String[] copy = new String[array.length];

      for(int i = 0; i < array.length; ++i) {
         copy[i] = array[i].toLowerCase().intern();
      }

      return copy;
   }

   public static <E> Collection<E> addAll(Collection<E> dest, Collection<? extends E>... src) {
      for(Collection<? extends E> c : src) {
         dest.addAll(c);
      }

      return dest;
   }
}
