package me.lucko.spark.common.sampler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.lucko.spark.proto.SparkProtos;

public interface ThreadGrouper {
   ThreadGrouper BY_NAME = new ThreadGrouper() {
      public String getGroup(long threadId, String threadName) {
         return threadName;
      }

      public SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper asProto() {
         return SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper.BY_NAME;
      }
   };
   ThreadGrouper BY_POOL = new ThreadGrouper() {
      private final Map<Long, String> cache = new ConcurrentHashMap();
      private final Pattern pattern = Pattern.compile("^(.*?)[-# ]+\\d+$");

      public String getGroup(long threadId, String threadName) {
         String group = (String)this.cache.get(Long.valueOf(threadId));
         if (group != null) {
            return group;
         } else {
            Matcher matcher = this.pattern.matcher(threadName);
            if (!matcher.matches()) {
               return threadName;
            } else {
               group = matcher.group(1).trim() + " (Combined)";
               this.cache.put(Long.valueOf(threadId), group);
               return group;
            }
         }
      }

      public SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper asProto() {
         return SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper.BY_POOL;
      }
   };
   ThreadGrouper AS_ONE = new ThreadGrouper() {
      public String getGroup(long threadId, String threadName) {
         return "All";
      }

      public SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper asProto() {
         return SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper.AS_ONE;
      }
   };

   String getGroup(long var1, String var3);

   SparkProtos.SamplerMetadata.DataAggregator.ThreadGrouper asProto();
}
