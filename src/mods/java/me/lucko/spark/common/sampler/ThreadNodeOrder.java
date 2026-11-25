package me.lucko.spark.common.sampler;

import java.util.Comparator;
import java.util.Map.Entry;
import me.lucko.spark.common.sampler.node.ThreadNode;

public enum ThreadNodeOrder implements Comparator<Entry<String, ThreadNode>> {
   BY_NAME {
      public int compare(Entry<String, ThreadNode> o1, Entry<String, ThreadNode> o2) {
         return ((String)o1.getKey()).compareTo((String)o2.getKey());
      }
   },
   BY_TIME {
      public int compare(Entry<String, ThreadNode> o1, Entry<String, ThreadNode> o2) {
         return -Double.compare(((ThreadNode)o1.getValue()).getTotalTime(), ((ThreadNode)o2.getValue()).getTotalTime());
      }
   };

   private ThreadNodeOrder() {
   }
}
