package com.github.benmanes.caffeine.cache.stats;

import java.util.concurrent.atomic.LongAdder;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ConcurrentStatsCounter implements StatsCounter {
   private final LongAdder hitCount = new LongAdder();
   private final LongAdder missCount = new LongAdder();
   private final LongAdder loadSuccessCount = new LongAdder();
   private final LongAdder loadFailureCount = new LongAdder();
   private final LongAdder totalLoadTime = new LongAdder();
   private final LongAdder evictionCount = new LongAdder();
   private final LongAdder evictionWeight = new LongAdder();

   public ConcurrentStatsCounter() {
   }

   public void recordHits(int count) {
      this.hitCount.add((long)count);
   }

   public void recordMisses(int count) {
      this.missCount.add((long)count);
   }

   public void recordLoadSuccess(long loadTime) {
      this.loadSuccessCount.increment();
      this.totalLoadTime.add(loadTime);
   }

   public void recordLoadFailure(long loadTime) {
      this.loadFailureCount.increment();
      this.totalLoadTime.add(loadTime);
   }

   public void recordEviction() {
      this.evictionCount.increment();
   }

   public void recordEviction(int weight) {
      this.evictionCount.increment();
      this.evictionWeight.add((long)weight);
   }

   public CacheStats snapshot() {
      return new CacheStats(this.hitCount.sum(), this.missCount.sum(), this.loadSuccessCount.sum(), this.loadFailureCount.sum(), this.totalLoadTime.sum(), this.evictionCount.sum(), this.evictionWeight.sum());
   }

   public void incrementBy(@NonNull StatsCounter other) {
      CacheStats otherStats = other.snapshot();
      this.hitCount.add(otherStats.hitCount());
      this.missCount.add(otherStats.missCount());
      this.loadSuccessCount.add(otherStats.loadSuccessCount());
      this.loadFailureCount.add(otherStats.loadFailureCount());
      this.totalLoadTime.add(otherStats.totalLoadTime());
      this.evictionCount.add(otherStats.evictionCount());
      this.evictionWeight.add(otherStats.evictionWeight());
   }

   public String toString() {
      return this.snapshot().toString();
   }
}
