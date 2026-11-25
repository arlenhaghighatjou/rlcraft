package me.lucko.spark.common.sampler.java;

import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import me.lucko.spark.common.sampler.ThreadGrouper;
import me.lucko.spark.common.sampler.node.ThreadNode;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.proto.SparkProtos;

public class TickedDataAggregator extends JavaDataAggregator {
   private final TickHook tickHook;
   private final long tickLengthThreshold;
   private final int expectedSize;
   private final Object mutex = new Object();
   private int currentTick = -1;
   private TickedDataAggregator.TickList currentData = new TickedDataAggregator.TickList(0);

   public TickedDataAggregator(ExecutorService workerPool, ThreadGrouper threadGrouper, int interval, boolean ignoreSleeping, boolean ignoreNative, TickHook tickHook, int tickLengthThreshold) {
      super(workerPool, threadGrouper, interval, ignoreSleeping, ignoreNative);
      this.tickHook = tickHook;
      this.tickLengthThreshold = TimeUnit.MILLISECONDS.toMicros((long)tickLengthThreshold);
      double intervalMilliseconds = (double)interval / 1000.0D;
      this.expectedSize = (int)(50.0D / intervalMilliseconds + 10.0D);
   }

   public SparkProtos.SamplerMetadata.DataAggregator getMetadata() {
      return (SparkProtos.SamplerMetadata.DataAggregator)SparkProtos.SamplerMetadata.DataAggregator.newBuilder().setType(SparkProtos.SamplerMetadata.DataAggregator.Type.TICKED).setThreadGrouper(this.threadGrouper.asProto()).setTickLengthThreshold(this.tickLengthThreshold).build();
   }

   public void insertData(ThreadInfo threadInfo) {
      synchronized(this.mutex) {
         int tick = this.tickHook.getCurrentTick();
         if (this.currentTick != tick) {
            this.pushCurrentTick();
            this.currentTick = tick;
            this.currentData = new TickedDataAggregator.TickList(this.expectedSize);
         }

         this.currentData.addData(threadInfo);
      }
   }

   private void pushCurrentTick() {
      TickedDataAggregator.TickList currentData = this.currentData;
      int tickLengthMicros = currentData.getList().size() * this.interval;
      if ((long)tickLengthMicros >= this.tickLengthThreshold) {
         this.workerPool.submit(currentData);
      }
   }

   public Map<String, ThreadNode> getData() {
      synchronized(this.mutex) {
         this.pushCurrentTick();
      }

      return super.getData();
   }

   private final class TickList implements Runnable {
      private final List<ThreadInfo> list;

      TickList(int expectedSize) {
         this.list = new ArrayList(expectedSize);
      }

      public void run() {
         for(ThreadInfo data : this.list) {
            TickedDataAggregator.this.writeData(data);
         }

      }

      public List<ThreadInfo> getList() {
         return this.list;
      }

      public void addData(ThreadInfo data) {
         this.list.add(data);
      }
   }
}
