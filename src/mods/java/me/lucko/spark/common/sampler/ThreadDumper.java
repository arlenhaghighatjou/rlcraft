package me.lucko.spark.common.sampler;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import me.lucko.spark.common.util.ThreadFinder;
import me.lucko.spark.proto.SparkProtos;

public interface ThreadDumper {
   ThreadDumper ALL = new ThreadDumper() {
      public ThreadInfo[] dumpThreads(ThreadMXBean threadBean) {
         return threadBean.dumpAllThreads(false, false);
      }

      public SparkProtos.SamplerMetadata.ThreadDumper getMetadata() {
         return (SparkProtos.SamplerMetadata.ThreadDumper)SparkProtos.SamplerMetadata.ThreadDumper.newBuilder().setType(SparkProtos.SamplerMetadata.ThreadDumper.Type.ALL).build();
      }
   };

   ThreadInfo[] dumpThreads(ThreadMXBean var1);

   SparkProtos.SamplerMetadata.ThreadDumper getMetadata();

   public static final class GameThread implements Supplier<ThreadDumper> {
      private ThreadDumper.Specific dumper = null;

      public GameThread() {
      }

      public ThreadDumper get() {
         return (ThreadDumper)Objects.requireNonNull(this.dumper, "dumper");
      }

      public void ensureSetup() {
         if (this.dumper == null) {
            this.dumper = new ThreadDumper.Specific(new long[]{Thread.currentThread().getId()});
         }

      }
   }

   public static final class Regex implements ThreadDumper {
      private final ThreadFinder threadFinder = new ThreadFinder();
      private final Set<Pattern> namePatterns;
      private final Map<Long, Boolean> cache = new HashMap();

      public Regex(Set<String> namePatterns) {
         this.namePatterns = (Set)namePatterns.stream().map((regex) -> {
            try {
               return (void)Pattern.compile(regex, 2);
            } catch (PatternSyntaxException var2) {
               return (void)null;
            }
         }).filter(Objects::nonNull).collect(Collectors.toSet());
      }

      public ThreadInfo[] dumpThreads(ThreadMXBean threadBean) {
         return (ThreadInfo[])this.threadFinder.getThreads().filter((thread) -> {
            Boolean result = (Boolean)this.cache.get(Long.valueOf(thread.getId()));
            if (result != null) {
               return result.booleanValue();
            } else {
               for(Pattern pattern : this.namePatterns) {
                  if (pattern.matcher(thread.getName()).matches()) {
                     this.cache.put(Long.valueOf(thread.getId()), Boolean.valueOf(true));
                     return true;
                  }
               }

               this.cache.put(Long.valueOf(thread.getId()), Boolean.valueOf(false));
               return false;
            }
         }).map((thread) -> {
            return threadBean.getThreadInfo(thread.getId(), Integer.MAX_VALUE);
         }).filter(Objects::nonNull).toArray((x$0) -> {
            return new ThreadInfo[x$0];
         });
      }

      public SparkProtos.SamplerMetadata.ThreadDumper getMetadata() {
         return (SparkProtos.SamplerMetadata.ThreadDumper)SparkProtos.SamplerMetadata.ThreadDumper.newBuilder().setType(SparkProtos.SamplerMetadata.ThreadDumper.Type.REGEX).addAllPatterns((Iterable)this.namePatterns.stream().map(Pattern::pattern).collect(Collectors.toList())).build();
      }
   }

   public static final class Specific implements ThreadDumper {
      private final long[] ids;
      private Set<Thread> threads;
      private Set<String> threadNamesLowerCase;

      public Specific(long[] ids) {
         this.ids = ids;
      }

      public Specific(Set<String> names) {
         this.threadNamesLowerCase = (Set)names.stream().map(String::toLowerCase).collect(Collectors.toSet());
         this.ids = (new ThreadFinder()).getThreads().filter((t) -> {
            return (void)this.threadNamesLowerCase.contains(t.getName().toLowerCase());
         }).mapToLong(Thread::getId).toArray();
         Arrays.sort(this.ids);
      }

      public Set<Thread> getThreads() {
         if (this.threads == null) {
            this.threads = (Set)(new ThreadFinder()).getThreads().filter((t) -> {
               return Arrays.binarySearch(this.ids, t.getId()) >= 0;
            }).collect(Collectors.toSet());
         }

         return this.threads;
      }

      public Set<String> getThreadNames() {
         if (this.threadNamesLowerCase == null) {
            this.threadNamesLowerCase = (Set)this.getThreads().stream().map((t) -> {
               return t.getName().toLowerCase();
            }).collect(Collectors.toSet());
         }

         return this.threadNamesLowerCase;
      }

      public ThreadInfo[] dumpThreads(ThreadMXBean threadBean) {
         return threadBean.getThreadInfo(this.ids, Integer.MAX_VALUE);
      }

      public SparkProtos.SamplerMetadata.ThreadDumper getMetadata() {
         return (SparkProtos.SamplerMetadata.ThreadDumper)SparkProtos.SamplerMetadata.ThreadDumper.newBuilder().setType(SparkProtos.SamplerMetadata.ThreadDumper.Type.SPECIFIC).addAllIds((Iterable)Arrays.stream(this.ids).boxed().collect(Collectors.toList())).build();
      }
   }
}
