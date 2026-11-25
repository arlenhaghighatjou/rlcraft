package nonapi.io.github.classgraph.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class CollectionUtils {
   private CollectionUtils() {
   }

   public static <T extends Comparable<? super T>> void sortIfNotEmpty(List<T> list) {
      if (!list.isEmpty()) {
         Collections.sort(list);
      }

   }

   public static <T> void sortIfNotEmpty(List<T> list, Comparator<? super T> comparator) {
      if (!list.isEmpty()) {
         Collections.sort(list, comparator);
      }

   }
}
