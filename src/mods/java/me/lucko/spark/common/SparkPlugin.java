package me.lucko.spark.common;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.stream.Stream;
import me.lucko.spark.api.Spark;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.sampler.ThreadDumper;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.tick.TickReporter;
import me.lucko.spark.common.util.ClassSourceLookup;

public interface SparkPlugin {
   String getVersion();

   Path getPluginDirectory();

   String getCommandName();

   Stream<? extends CommandSender> getCommandSenders();

   void executeAsync(Runnable var1);

   void log(Level var1, String var2);

   default ThreadDumper getDefaultThreadDumper() {
      return ThreadDumper.ALL;
   }

   default TickHook createTickHook() {
      return null;
   }

   default TickReporter createTickReporter() {
      return null;
   }

   default ClassSourceLookup createClassSourceLookup() {
      return ClassSourceLookup.NO_OP;
   }

   PlatformInfo getPlatformInfo();

   default void registerApi(Spark api) {
   }
}
