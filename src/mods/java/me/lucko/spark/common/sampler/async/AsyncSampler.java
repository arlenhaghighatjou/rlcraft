package me.lucko.spark.common.sampler.async;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.sampler.AbstractSampler;
import me.lucko.spark.common.sampler.ThreadDumper;
import me.lucko.spark.common.sampler.ThreadGrouper;
import me.lucko.spark.common.sampler.async.jfr.JfrReader;
import me.lucko.spark.common.sampler.node.MergeMode;
import me.lucko.spark.common.sampler.node.ThreadNode;
import me.lucko.spark.common.util.ClassSourceLookup;
import me.lucko.spark.common.util.TemporaryFiles;
import me.lucko.spark.lib.asyncprofiler.AsyncProfiler;
import me.lucko.spark.proto.SparkProtos;

public class AsyncSampler extends AbstractSampler {
   private final AsyncProfiler profiler = AsyncProfilerAccess.INSTANCE.getProfiler();
   private final AsyncDataAggregator dataAggregator;
   private boolean outputComplete = false;
   private Path outputFile;
   private ScheduledExecutorService timeoutExecutor;

   public AsyncSampler(int interval, ThreadDumper threadDumper, ThreadGrouper threadGrouper, long endTime) {
      super(interval, threadDumper, endTime);
      this.dataAggregator = new AsyncDataAggregator(threadGrouper);
   }

   private String execute(String command) {
      try {
         return this.profiler.execute(command);
      } catch (IOException var3) {
         throw new RuntimeException("Exception whilst executing profiler command", var3);
      }
   }

   public void start() {
      this.startTime = System.currentTimeMillis();

      try {
         this.outputFile = TemporaryFiles.create("spark-profile-", ".jfr.tmp");
      } catch (IOException var6) {
         throw new RuntimeException("Unable to create temporary output file", var6);
      }

      String command = "start,event=cpu,interval=" + this.interval + "us,threads,jfr,file=" + this.outputFile.toString();
      if (this.threadDumper instanceof ThreadDumper.Specific) {
         command = command + ",filter";
      }

      String resp = this.execute(command).trim();
      if (!resp.equalsIgnoreCase("profiling started")) {
         throw new RuntimeException("Unexpected response: " + resp);
      } else {
         if (this.threadDumper instanceof ThreadDumper.Specific) {
            ThreadDumper.Specific threadDumper = (ThreadDumper.Specific)this.threadDumper;

            for(Thread thread : threadDumper.getThreads()) {
               this.profiler.addThread(thread);
            }
         }

         this.scheduleTimeout();
      }
   }

   private void scheduleTimeout() {
      if (this.endTime != -1L) {
         long delay = this.endTime - System.currentTimeMillis();
         if (delay > 0L) {
            this.timeoutExecutor = Executors.newSingleThreadScheduledExecutor((new ThreadFactoryBuilder()).setNameFormat("spark-asyncsampler-timeout-thread").build());
            this.timeoutExecutor.schedule(() -> {
               this.stop();
               this.future.complete(this);
            }, delay, TimeUnit.MILLISECONDS);
         }
      }
   }

   public void stop() {
      this.profiler.stop();
      if (this.timeoutExecutor != null) {
         this.timeoutExecutor.shutdown();
         this.timeoutExecutor = null;
      }

   }

   public SparkProtos.SamplerData toProto(PlatformInfo platformInfo, CommandSender creator, Comparator<? super Entry<String, ThreadNode>> outputOrder, String comment, MergeMode mergeMode, ClassSourceLookup classSourceLookup) {
      SparkProtos.SamplerMetadata.Builder metadata = SparkProtos.SamplerMetadata.newBuilder().setPlatformMetadata(platformInfo.toData().toProto()).setCreator(creator.toData().toProto()).setStartTime(this.startTime).setInterval(this.interval).setThreadDumper(this.threadDumper.getMetadata()).setDataAggregator(this.dataAggregator.getMetadata());
      if (comment != null) {
         metadata.setComment(comment);
      }

      SparkProtos.SamplerData.Builder proto = SparkProtos.SamplerData.newBuilder();
      proto.setMetadata((SparkProtos.SamplerMetadata)metadata.build());
      this.aggregateOutput();
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

   private void aggregateOutput() {
      if (!this.outputComplete) {
         this.outputComplete = true;
         Predicate<String> threadFilter;
         if (this.threadDumper instanceof ThreadDumper.Specific) {
            ThreadDumper.Specific threadDumper = (ThreadDumper.Specific)this.threadDumper;
            threadFilter = (n) -> {
               return threadDumper.getThreadNames().contains(n.toLowerCase());
            };
         } else {
            threadFilter = (n) -> {
               return true;
            };
         }

         try {
            JfrReader reader = new JfrReader(this.outputFile);

            try {
               this.readSegments(reader, threadFilter);
            } catch (Throwable var7) {
               try {
                  reader.close();
               } catch (Throwable var5) {
                  var7.addSuppressed(var5);
               }

               throw var7;
            }

            reader.close();
         } catch (IOException var8) {
            throw new RuntimeException("Read error", var8);
         }

         try {
            Files.deleteIfExists(this.outputFile);
         } catch (IOException var6) {
            ;
         }

      }
   }

   private void readSegments(JfrReader reader, Predicate<String> threadFilter) throws IOException {
      List<JfrReader.ExecutionSample> samples = reader.<JfrReader.ExecutionSample>readAllEvents(JfrReader.ExecutionSample.class);

      for(int i = 0; i < samples.size(); ++i) {
         JfrReader.ExecutionSample sample = (JfrReader.ExecutionSample)samples.get(i);
         long duration;
         if (i == 0) {
            duration = (long)this.interval;
         } else {
            duration = TimeUnit.NANOSECONDS.toMicros(sample.time - ((JfrReader.ExecutionSample)samples.get(i - 1)).time);
         }

         String threadName = reader.threads.get((long)sample.tid);
         if (threadFilter.test(threadName)) {
            ProfileSegment segment = parseSegment(reader, sample, threadName, duration);
            this.dataAggregator.insertData(segment);
         }
      }

   }

   private static ProfileSegment parseSegment(JfrReader reader, JfrReader.ExecutionSample sample, String threadName, long duration) {
      JfrReader.StackTrace stackTrace = reader.stackTraces.get((long)sample.stackTraceId);
      int len = stackTrace.methods.length;
      AsyncStackTraceElement[] stack = new AsyncStackTraceElement[len];

      for(int i = 0; i < len; ++i) {
         stack[i] = parseStackFrame(reader, stackTrace.methods[i]);
      }

      return new ProfileSegment(sample.tid, threadName, stack, duration);
   }

   private static AsyncStackTraceElement parseStackFrame(JfrReader reader, long methodId) {
      AsyncStackTraceElement result = reader.stackFrames.get(methodId);
      if (result != null) {
         return result;
      } else {
         JfrReader.MethodRef methodRef = reader.methods.get(methodId);
         JfrReader.ClassRef classRef = reader.classes.get(methodRef.cls);
         byte[] className = reader.symbols.get(classRef.name);
         byte[] methodName = reader.symbols.get(methodRef.name);
         if (className != null && className.length != 0) {
            byte[] methodDesc = reader.symbols.get(methodRef.sig);
            result = new AsyncStackTraceElement((new String(className, StandardCharsets.UTF_8)).replace('/', '.'), new String(methodName, StandardCharsets.UTF_8), new String(methodDesc, StandardCharsets.UTF_8));
         } else {
            result = new AsyncStackTraceElement("native", new String(methodName, StandardCharsets.UTF_8), (String)null);
         }

         reader.stackFrames.put(methodId, result);
         return result;
      }
   }
}
