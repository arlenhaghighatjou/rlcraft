package me.lucko.spark.common.sampler;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractSampler implements Sampler {
   protected final int interval;
   protected final ThreadDumper threadDumper;
   protected long startTime = -1L;
   protected final long endTime;
   protected final CompletableFuture<Sampler> future = new CompletableFuture();

   protected AbstractSampler(int interval, ThreadDumper threadDumper, long endTime) {
      this.interval = interval;
      this.threadDumper = threadDumper;
      this.endTime = endTime;
   }

   public long getStartTime() {
      if (this.startTime == -1L) {
         throw new IllegalStateException("Not yet started");
      } else {
         return this.startTime;
      }
   }

   public long getEndTime() {
      return this.endTime;
   }

   public CompletableFuture<Sampler> getFuture() {
      return this.future;
   }
}
