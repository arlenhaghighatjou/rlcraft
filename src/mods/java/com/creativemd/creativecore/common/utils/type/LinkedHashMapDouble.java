package com.creativemd.creativecore.common.utils.type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LinkedHashMapDouble<K> extends LinkedHashMap<K, Double> {
   public LinkedHashMapDouble() {
   }

   public LinkedHashMapDouble(Map<? extends K, ? extends Double> paramMap) {
      super(paramMap);
   }

   public Double put(K paramK, Double paramV) {
      Double value = (Double)this.get(paramK);
      if (value != null) {
         value = value.doubleValue() + paramV.doubleValue();
      } else {
         value = paramV;
      }

      return (Double)super.put(paramK, value);
   }

   public void putAll(Map<? extends K, ? extends Double> paramMap) {
      for(Entry<? extends K, ? extends Double> entry : paramMap.entrySet()) {
         this.put(entry.getKey(), (Double)entry.getValue());
      }

   }
}
