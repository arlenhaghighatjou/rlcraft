package mcjty.lostcities.varia;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Counter<T> {
   private Map<T, Integer> internalMap = new HashMap();

   public Counter() {
   }

   public void add(T key) {
      if (!this.internalMap.containsKey(key)) {
         this.internalMap.put(key, Integer.valueOf(0));
      }

      this.internalMap.put(key, Integer.valueOf(((Integer)this.internalMap.get(key)).intValue() + 1));
   }

   public Map<T, Integer> getMap() {
      return this.internalMap;
   }

   public int get(T key) {
      return this.internalMap.containsKey(key) ? ((Integer)this.internalMap.get(key)).intValue() : 0;
   }

   public T getMostOccuring() {
      T max = null;
      int maxCount = -1;

      for(Entry<T, Integer> entry : this.internalMap.entrySet()) {
         if (((Integer)entry.getValue()).intValue() > maxCount) {
            maxCount = ((Integer)entry.getValue()).intValue();
            max = (T)entry.getKey();
         }
      }

      return max;
   }
}
