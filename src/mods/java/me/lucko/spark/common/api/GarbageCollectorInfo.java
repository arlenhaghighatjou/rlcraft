package me.lucko.spark.common.api;

import me.lucko.spark.api.gc.GarbageCollector;
import me.lucko.spark.common.monitor.memory.GarbageCollectorStatistics;
import org.checkerframework.checker.nullness.qual.NonNull;

public class GarbageCollectorInfo implements GarbageCollector {
   private final String name;
   private final long totalCollections;
   private final long totalTime;
   private final double averageTime;
   private final long averageFrequency;

   public GarbageCollectorInfo(String name, GarbageCollectorStatistics stats, long serverUptime) {
      this.name = name;
      this.totalCollections = stats.getCollectionCount();
      this.totalTime = stats.getCollectionTime();
      double totalTimeDouble = (double)this.totalTime;
      this.averageTime = this.totalCollections == 0L ? 0.0D : totalTimeDouble / (double)this.totalCollections;
      this.averageFrequency = this.totalCollections == 0L ? 0L : (long)(((double)serverUptime - totalTimeDouble) / (double)this.totalCollections);
   }

   @NonNull
   public String name() {
      return this.name;
   }

   public long totalCollections() {
      return this.totalCollections;
   }

   public long totalTime() {
      return this.totalTime;
   }

   public double avgTime() {
      return this.averageTime;
   }

   public long avgFrequency() {
      return this.averageFrequency;
   }
}
