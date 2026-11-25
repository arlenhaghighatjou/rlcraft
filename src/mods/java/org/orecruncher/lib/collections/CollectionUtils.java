package org.orecruncher.lib.collections;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class CollectionUtils {
   private CollectionUtils() {
   }

   public static <K, V> void removeIf(@Nonnull Map<K, V> theMap, @Nonnull Predicate<? super Entry<K, V>> thePred) {
      ArrayList<K> keys = new ArrayList();

      for(Entry<K, V> entry : theMap.entrySet()) {
         if (thePred.test(entry)) {
            keys.add(entry.getKey());
         }
      }

      for(K key : keys) {
         theMap.remove(key);
      }

   }
}
