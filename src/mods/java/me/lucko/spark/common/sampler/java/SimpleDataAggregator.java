package me.lucko.spark.common.sampler.java;

import java.lang.management.ThreadInfo;
import java.util.concurrent.ExecutorService;
import me.lucko.spark.common.sampler.ThreadGrouper;
import me.lucko.spark.proto.SparkProtos;

public class SimpleDataAggregator extends JavaDataAggregator {
   public SimpleDataAggregator(ExecutorService workerPool, ThreadGrouper threadGrouper, int interval, boolean ignoreSleeping, boolean ignoreNative) {
      super(workerPool, threadGrouper, interval, ignoreSleeping, ignoreNative);
   }

   public SparkProtos.SamplerMetadata.DataAggregator getMetadata() {
      return (SparkProtos.SamplerMetadata.DataAggregator)SparkProtos.SamplerMetadata.DataAggregator.newBuilder().setType(SparkProtos.SamplerMetadata.DataAggregator.Type.SIMPLE).setThreadGrouper(this.threadGrouper.asProto()).build();
   }

   public void insertData(ThreadInfo threadInfo) {
      this.writeData(threadInfo);
   }
}
