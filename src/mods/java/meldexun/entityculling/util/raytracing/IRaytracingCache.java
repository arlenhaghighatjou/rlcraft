package meldexun.entityculling.util.raytracing;

import java.util.function.BooleanSupplier;

interface IRaytracingCache {
   boolean getOrSetCachedValue(int var1, int var2, int var3, BooleanSupplier var4);

   void clearCache();
}
