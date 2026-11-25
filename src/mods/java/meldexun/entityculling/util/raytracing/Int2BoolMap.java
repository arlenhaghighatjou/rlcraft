package meldexun.entityculling.util.raytracing;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import java.util.function.IntPredicate;

class Int2BoolMap extends Int2BooleanOpenHashMap {
   public Int2BoolMap() {
   }

   public Int2BoolMap(int expected, float f) {
      super(expected, f);
   }

   public boolean computeIfAbsent(int k, IntPredicate mappingFunction) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         boolean newValue = mappingFunction.test(k);
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   private int find(int k) {
      if (k == 0) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0) {
            return -(pos + 1);
         } else if (k == curr) {
            return pos;
         } else {
            while((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (k == curr) {
                  return pos;
               }
            }

            return -(pos + 1);
         }
      }
   }

   private void insert(int pos, int k, boolean v) {
      if (pos == this.n) {
         this.containsNullKey = true;
      }

      this.key[pos] = k;
      this.value[pos] = v;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

   }
}
