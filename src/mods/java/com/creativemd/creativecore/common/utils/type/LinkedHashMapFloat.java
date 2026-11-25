package com.creativemd.creativecore.common.utils.type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LinkedHashMapFloat<K> extends LinkedHashMap<K, Float> {
   public LinkedHashMapFloat() {
   }

   public LinkedHashMapFloat(Map<? extends K, ? extends Float> paramMap) {
      super(paramMap);
   }

   public Float put(K paramK, Float paramV) {
      Float value = (Float)this.get(paramK);
      if (value != null) {
         value = value.floatValue() + paramV.floatValue();
      } else {
         value = paramV;
      }

      return (Float)super.put(paramK, value);
   }

   public void putAll(Map<? extends K, ? extends Float> paramMap) {
      for(Entry<? extends K, ? extends Float> entry : paramMap.entrySet()) {
         this.put(entry.getKey(), (Float)entry.getValue());
      }

   }
}
