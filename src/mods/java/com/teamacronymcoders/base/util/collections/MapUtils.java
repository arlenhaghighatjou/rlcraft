package com.teamacronymcoders.base.util.collections;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class MapUtils {
   public MapUtils() {
   }

   public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
      List<Entry<K, V>> list = new LinkedList(map.entrySet());
      list.sort(Comparator.comparing((o) -> {
         return (Comparable)o.getValue();
      }));
      Map<K, V> result = new LinkedHashMap();

      for(Entry<K, V> entry : list) {
         result.put(entry.getKey(), entry.getValue());
      }

      return result;
   }
}
