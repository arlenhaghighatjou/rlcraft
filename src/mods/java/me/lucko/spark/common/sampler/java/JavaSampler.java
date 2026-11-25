package me.lucko.spark.common.sampler.java;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.sampler.AbstractSampler;
import me.lucko.spark.common.sampler.ThreadDumper;
import me.lucko.spark.common.sampler.ThreadGrouper;
import me.lucko.spark.common.sampler.node.MergeMode;
import me.lucko.spark.common.sampler.node.ThreadNode;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.util.ClassSourceLookup;
import me.lucko.spark.proto.SparkProtos;

public class JavaSampler extends AbstractSampler implements Runnable {
   private static final AtomicInteger THREAD_ID = new AtomicInteger(0);
   private final ScheduledExecutorService workerPool;
   private ScheduledFuture<?> task;
   private final ThreadMXBean threadBean;
   private final JavaDataAggregator dataAggregator;

   public JavaSampler(int interval, ThreadDumper threadDumper, ThreadGrouper threadGrouper, long endTime, boolean ignoreSleeping, boolean ignoreNative) {
      super(interval, threadDumper, endTime);
      this.workerPool = Executors.newScheduledThreadPool(6, (new ThreadFactoryBuilder()).setNameFormat("spark-worker-" + THREAD_ID.getAndIncrement() + "-%d").build());
      this.threadBean = ManagementFactory.getThreadMXBean();
      this.dataAggregator = new SimpleDataAggregator(this.workerPool, threadGrouper, interval, ignoreSleeping, ignoreNative);
   }

   public JavaSampler(int interval, ThreadDumper threadDumper, ThreadGrouper threadGrouper, long endTime, boolean ignoreSleeping, boolean ignoreNative, TickHook tickHook, int tickLengthThreshold) {
      super(interval, threadDumper, endTime);
      this.workerPool = Executors.newScheduledThreadPool(6, (new ThreadFactoryBuilder()).setNameFormat("spark-worker-" + THREAD_ID.getAndIncrement() + "-%d").build());
      this.threadBean = ManagementFactory.getThreadMXBean();
      this.dataAggregator = new TickedDataAggregator(this.workerPool, threadGrouper, interval, ignoreSleeping, ignoreNative, tickHook, tickLengthThreshold);
   }

   public void start() {
      this.startTime = System.currentTimeMillis();
      this.task = this.workerPool.scheduleAtFixedRate(this, 0L, (long)this.interval, TimeUnit.MICROSECONDS);
   }

   public void stop() {
      this.task.cancel(false);
   }

   public void run() {
      try {
         if (this.endTime != -1L && this.endTime <= System.currentTimeMillis()) {
            this.future.complete(this);
            this.stop();
            return;
         }

         ThreadInfo[] threadDumps = this.threadDumper.dumpThreads(this.threadBean);
         this.workerPool.execute(new JavaSampler.InsertDataTask(this.dataAggregator, threadDumps));
      } catch (Throwable var2) {
         this.future.completeExceptionally(var2);
         this.stop();
      }

   }

   public SparkProtos.SamplerData toProto(PlatformInfo platformInfo, CommandSender creator, Comparator<? super Entry<String, ThreadNode>> outputOrder, String comment, MergeMode mergeMode, ClassSourceLookup classSourceLookup) {
      SparkProtos.SamplerMetadata.Builder metadata = SparkProtos.SamplerMetadata.newBuilder().setPlatformMetadata(platformInfo.toData().toProto()).setCreator(creator.toData().toProto()).setStartTime(this.startTime).setInterval(this.interval).setThreadDumper(this.threadDumper.getMetadata()).setDataAggregator(this.dataAggregator.getMetadata());
      if (comment != null) {
         metadata.setComment(comment);
      }

      SparkProtos.SamplerData.Builder proto = SparkProtos.SamplerData.newBuilder();
      proto.setMetadata((SparkProtos.SamplerMetadata)metadata.build());
      List<Entry<String, ThreadNode>> data = new ArrayList(this.dataAggregator.getData().entrySet());
      data.sort(outputOrder);
      ClassSourceLookup.Visitor classSourceVisitor = ClassSourceLookup.createVisitor(classSourceLookup);

      for(Entry<String, ThreadNode> entry : data) {
         proto.addThreads(((ThreadNode)entry.getValue()).toProto(mergeMode));
         classSourceVisitor.visit((ThreadNode)entry.getValue());
      }

      if (classSourceVisitor.hasMappings()) {
         proto.putAllClassSources(classSourceVisitor.getMapping());
      }

      return (SparkProtos.SamplerData)proto.build();
   }

   private static final class InsertDataTask implements Runnable {
      private final JavaDataAggregator dataAggregator;
      private final ThreadInfo[] threadDumps;

      InsertDataTask(JavaDataAggregator dataAggregator, ThreadInfo[] threadDumps) {
         this.dataAggregator = dataAggregator;
         this.threadDumps = threadDumps;
      }

      public void run() {
         for(ThreadInfo threadInfo : this.threadDumps) {
            if (threadInfo.getThreadName() != null && threadInfo.getStackTrace() != null) {
               this.dataAggregator.insertData(threadInfo);
            }
         }

      }
   }
}
