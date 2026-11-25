package me.lucko.spark.common.sampler.async;

import me.lucko.spark.common.sampler.ThreadGrouper;
import me.lucko.spark.common.sampler.aggregator.AbstractDataAggregator;
import me.lucko.spark.common.sampler.node.StackTraceNode;
import me.lucko.spark.common.sampler.node.ThreadNode;
import me.lucko.spark.proto.SparkProtos;

public class AsyncDataAggregator extends AbstractDataAggregator {
   private static final StackTraceNode.Describer<AsyncStackTraceElement> STACK_TRACE_DESCRIBER = (element, parent) -> {
      return (void)(new StackTraceNode.Description(element.getClassName(), element.getMethodName(), element.getMethodDescription()));
   };

   protected AsyncDataAggregator(ThreadGrouper threadGrouper) {
      super(threadGrouper);
   }

   public SparkProtos.SamplerMetadata.DataAggregator getMetadata() {
      return (SparkProtos.SamplerMetadata.DataAggregator)SparkProtos.SamplerMetadata.DataAggregator.newBuilder().setType(SparkProtos.SamplerMetadata.DataAggregator.Type.SIMPLE).setThreadGrouper(this.threadGrouper.asProto()).build();
   }

   public void insertData(ProfileSegment element) {
      try {
         ThreadNode node = this.getNode(this.threadGrouper.getGroup((long)element.getNativeThreadId(), element.getThreadName()));
         node.log(STACK_TRACE_DESCRIBER, element.getStackTrace(), element.getTime());
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }
}
