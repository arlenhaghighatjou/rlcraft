package meldexun.entityculling.util.raytracing;

import java.util.function.BooleanSupplier;
import java.util.function.IntPredicate;

class RaytracingMapCache implements IRaytracingCache {
   private final Int2BoolMap map = new Int2BoolMap();

   RaytracingMapCache() {
   }

   public boolean getOrSetCachedValue(int x, int y, int z, BooleanSupplier function) {
      int i = this.index(x, y, z);
      return i < 0 ? function.getAsBoolean() : this.map.computeIfAbsent(i, (k) -> {
         return function.getAsBoolean();
      });
   }

   private int index(int x, int y, int z) {
      x = x + 512;
      if (x >= 0 && x < 1024) {
         y = y + 512;
         if (y >= 0 && y < 1024) {
            z = z + 512;
            return z >= 0 && z < 1024 ? z << 20 | y << 10 | x : -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public void clearCache() {
      this.map.clear();
   }
}
