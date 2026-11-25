package com.creativemd.creativecore.common.utils.type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LinkedHashMapInteger<K> extends LinkedHashMap<K, Integer> {
   public LinkedHashMapInteger() {
   }

   public LinkedHashMapInteger(Map<? extends K, ? extends Integer> paramMap) {
      super(paramMap);
   }

   public Integer put(K paramK, Integer paramV) {
      Integer value = (Integer)this.get(paramK);
      if (value != null) {
         value = value.intValue() + paramV.intValue();
      } else {
         value = paramV;
      }

      return (Integer)super.put(paramK, value);
   }

   public void putAll(Map<? extends K, ? extends Integer> paramMap) {
      for(Entry<? extends K, ? extends Integer> entry : paramMap.entrySet()) {
         this.put(entry.getKey(), (Integer)entry.getValue());
      }

   }
}
