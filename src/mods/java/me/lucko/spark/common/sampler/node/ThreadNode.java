package me.lucko.spark.common.sampler.node;

import me.lucko.spark.proto.SparkProtos;

public final class ThreadNode extends AbstractNode {
   private final String threadName;

   public ThreadNode(String threadName) {
      this.threadName = threadName;
   }

   public SparkProtos.ThreadNode toProto(MergeMode mergeMode) {
      SparkProtos.ThreadNode.Builder proto = SparkProtos.ThreadNode.newBuilder().setName(this.threadName).setTime(this.getTotalTime());

      for(StackTraceNode child : this.exportChildren(mergeMode)) {
         proto.addChildren(child.toProto(mergeMode));
      }

      return (SparkProtos.ThreadNode)proto.build();
   }
}
