package me.lucko.spark.common.sampler.aggregator;

import java.util.Map;
import me.lucko.spark.common.sampler.node.ThreadNode;
import me.lucko.spark.proto.SparkProtos;

public interface DataAggregator {
   Map<String, ThreadNode> getData();

   SparkProtos.SamplerMetadata.DataAggregator getMetadata();
}
