package com.github.benmanes.caffeine.cache.stats;

import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface StatsCounter {
   void recordHits(@NonNegative int var1);

   void recordMisses(@NonNegative int var1);

   void recordLoadSuccess(@NonNegative long var1);

   void recordLoadFailure(@NonNegative long var1);

   /** @deprecated */
   @Deprecated
   void recordEviction();

   default void recordEviction(@NonNegative int weight) {
      this.recordEviction();
   }

   @NonNull
   CacheStats snapshot();

   @NonNull
   static StatsCounter disabledStatsCounter() {
      return DisabledStatsCounter.INSTANCE;
   }

   @NonNull
   static StatsCounter guardedStatsCounter(@NonNull StatsCounter statsCounter) {
      return new GuardedStatsCounter(statsCounter);
   }
}
