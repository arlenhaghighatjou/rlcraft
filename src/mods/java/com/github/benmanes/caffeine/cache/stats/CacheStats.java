package com.github.benmanes.caffeine.cache.stats;

import com.google.errorprone.annotations.Immutable;
import java.util.Objects;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

@Immutable
public final class CacheStats {
   private static final CacheStats EMPTY_STATS = new CacheStats(0L, 0L, 0L, 0L, 0L, 0L, 0L);
   private final long hitCount;
   private final long missCount;
   private final long loadSuccessCount;
   private final long loadFailureCount;
   private final long totalLoadTime;
   private final long evictionCount;
   private final long evictionWeight;

   /** @deprecated */
   @Deprecated
   public CacheStats(@NonNegative long hitCount, @NonNegative long missCount, @NonNegative long loadSuccessCount, @NonNegative long loadFailureCount, @NonNegative long totalLoadTime, @NonNegative long evictionCount) {
      this(hitCount, missCount, loadSuccessCount, loadFailureCount, totalLoadTime, evictionCount, 0L);
   }

   public CacheStats(@NonNegative long hitCount, @NonNegative long missCount, @NonNegative long loadSuccessCount, @NonNegative long loadFailureCount, @NonNegative long totalLoadTime, @NonNegative long evictionCount, @NonNegative long evictionWeight) {
      if (hitCount >= 0L && missCount >= 0L && loadSuccessCount >= 0L && loadFailureCount >= 0L && totalLoadTime >= 0L && evictionCount >= 0L && evictionWeight >= 0L) {
         this.hitCount = hitCount;
         this.missCount = missCount;
         this.loadSuccessCount = loadSuccessCount;
         this.loadFailureCount = loadFailureCount;
         this.totalLoadTime = totalLoadTime;
         this.evictionCount = evictionCount;
         this.evictionWeight = evictionWeight;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @NonNull
   public static CacheStats empty() {
      return EMPTY_STATS;
   }

   @NonNegative
   public long requestCount() {
      return this.hitCount + this.missCount;
   }

   @NonNegative
   public long hitCount() {
      return this.hitCount;
   }

   @NonNegative
   public double hitRate() {
      long requestCount = this.requestCount();
      return requestCount == 0L ? 1.0D : (double)this.hitCount / (double)requestCount;
   }

   @NonNegative
   public long missCount() {
      return this.missCount;
   }

   @NonNegative
   public double missRate() {
      long requestCount = this.requestCount();
      return requestCount == 0L ? 0.0D : (double)this.missCount / (double)requestCount;
   }

   @NonNegative
   public long loadCount() {
      return this.loadSuccessCount + this.loadFailureCount;
   }

   @NonNegative
   public long loadSuccessCount() {
      return this.loadSuccessCount;
   }

   @NonNegative
   public long loadFailureCount() {
      return this.loadFailureCount;
   }

   @NonNegative
   public double loadFailureRate() {
      long totalLoadCount = this.loadSuccessCount + this.loadFailureCount;
      return totalLoadCount == 0L ? 0.0D : (double)this.loadFailureCount / (double)totalLoadCount;
   }

   @NonNegative
   public long totalLoadTime() {
      return this.totalLoadTime;
   }

   @NonNegative
   public double averageLoadPenalty() {
      long totalLoadCount = this.loadSuccessCount + this.loadFailureCount;
      return totalLoadCount == 0L ? 0.0D : (double)this.totalLoadTime / (double)totalLoadCount;
   }

   @NonNegative
   public long evictionCount() {
      return this.evictionCount;
   }

   @NonNegative
   public long evictionWeight() {
      return this.evictionWeight;
   }

   @NonNull
   public CacheStats minus(@NonNull CacheStats other) {
      return new CacheStats(Math.max(0L, this.hitCount - other.hitCount), Math.max(0L, this.missCount - other.missCount), Math.max(0L, this.loadSuccessCount - other.loadSuccessCount), Math.max(0L, this.loadFailureCount - other.loadFailureCount), Math.max(0L, this.totalLoadTime - other.totalLoadTime), Math.max(0L, this.evictionCount - other.evictionCount), Math.max(0L, this.evictionWeight - other.evictionWeight));
   }

   @NonNull
   public CacheStats plus(@NonNull CacheStats other) {
      return new CacheStats(this.hitCount + other.hitCount, this.missCount + other.missCount, this.loadSuccessCount + other.loadSuccessCount, this.loadFailureCount + other.loadFailureCount, this.totalLoadTime + other.totalLoadTime, this.evictionCount + other.evictionCount, this.evictionWeight + other.evictionWeight);
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.hitCount, this.missCount, this.loadSuccessCount, this.loadFailureCount, this.totalLoadTime, this.evictionCount, this.evictionWeight});
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof CacheStats)) {
         return false;
      } else {
         CacheStats other = (CacheStats)o;
         return this.hitCount == other.hitCount && this.missCount == other.missCount && this.loadSuccessCount == other.loadSuccessCount && this.loadFailureCount == other.loadFailureCount && this.totalLoadTime == other.totalLoadTime && this.evictionCount == other.evictionCount && this.evictionWeight == other.evictionWeight;
      }
   }

   public String toString() {
      return this.getClass().getSimpleName() + '{' + "hitCount=" + this.hitCount + ", missCount=" + this.missCount + ", loadSuccessCount=" + this.loadSuccessCount + ", loadFailureCount=" + this.loadFailureCount + ", totalLoadTime=" + this.totalLoadTime + ", evictionCount=" + this.evictionCount + ", evictionWeight=" + this.evictionWeight + '}';
   }
}
