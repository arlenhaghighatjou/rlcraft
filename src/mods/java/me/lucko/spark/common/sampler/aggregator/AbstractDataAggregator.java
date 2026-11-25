package me.lucko.spark.common.sampler.aggregator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import me.lucko.spark.common.sampler.ThreadGrouper;
import me.lucko.spark.common.sampler.node.ThreadNode;

public abstract class AbstractDataAggregator implements DataAggregator {
   protected final Map<String, ThreadNode> threadData = new ConcurrentHashMap();
   protected final ThreadGrouper threadGrouper;

   protected AbstractDataAggregator(ThreadGrouper threadGrouper) {
      this.threadGrouper = threadGrouper;
   }

   protected ThreadNode getNode(String group) {
      ThreadNode node = (ThreadNode)this.threadData.get(group);
      return node != null ? node : (ThreadNode)this.threadData.computeIfAbsent(group, ThreadNode::new);
   }

   public Map<String, ThreadNode> getData() {
      return this.threadData;
   }
}
