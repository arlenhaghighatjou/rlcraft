package me.lucko.spark.common.sampler;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.sampler.node.MergeMode;
import me.lucko.spark.common.sampler.node.ThreadNode;
import me.lucko.spark.common.util.ClassSourceLookup;
import me.lucko.spark.proto.SparkProtos;

public interface Sampler {
   void start();

   void stop();

   long getStartTime();

   long getEndTime();

   CompletableFuture<Sampler> getFuture();

   SparkProtos.SamplerData toProto(PlatformInfo var1, CommandSender var2, Comparator<? super Entry<String, ThreadNode>> var3, String var4, MergeMode var5, ClassSourceLookup var6);
}
