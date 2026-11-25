package com.charles445.rltweaker.util;

import java.util.Collection;
import java.util.stream.Collectors;

public class CollectionUtil {
   public CollectionUtil() {
   }

   public static <T> Collection<T> copy(Collection<T> collection) {
      return (Collection)collection.stream().collect(Collectors.toList());
   }
}
