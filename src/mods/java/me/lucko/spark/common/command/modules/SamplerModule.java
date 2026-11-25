package me.lucko.spark.common.command.modules;

import com.google.common.collect.Iterables;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.activitylog.Activity;
import me.lucko.spark.common.command.Arguments;
import me.lucko.spark.common.command.Command;
import me.lucko.spark.common.command.CommandModule;
import me.lucko.spark.common.command.CommandResponseHandler;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.command.tabcomplete.CompletionSupplier;
import me.lucko.spark.common.command.tabcomplete.TabCompleter;
import me.lucko.spark.common.sampler.Sampler;
import me.lucko.spark.common.sampler.SamplerBuilder;
import me.lucko.spark.common.sampler.ThreadDumper;
import me.lucko.spark.common.sampler.ThreadGrouper;
import me.lucko.spark.common.sampler.ThreadNodeOrder;
import me.lucko.spark.common.sampler.async.AsyncSampler;
import me.lucko.spark.common.sampler.node.MergeMode;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.util.MethodDisambiguator;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.TextComponent;
import me.lucko.spark.lib.adventure.text.event.ClickEvent;
import me.lucko.spark.lib.adventure.text.format.NamedTextColor;
import me.lucko.spark.lib.okhttp3.MediaType;
import me.lucko.spark.lib.protobuf.AbstractMessageLite;
import me.lucko.spark.proto.SparkProtos;

public class SamplerModule implements CommandModule {
   private static final MediaType SPARK_SAMPLER_MEDIA_TYPE = MediaType.parse("application/x-spark-sampler");
   private Sampler activeSampler = null;

   public SamplerModule() {
   }

   public void close() {
      if (this.activeSampler != null) {
         this.activeSampler.stop();
         this.activeSampler = null;
      }

   }

   public void registerCommands(Consumer<Command> consumer) {
      consumer.accept(Command.builder().aliases("profiler", "sampler").argumentUsage("info", (String)null).argumentUsage("stop", (String)null).argumentUsage("cancel", (String)null).argumentUsage("interval", "interval millis").argumentUsage("thread", "thread name").argumentUsage("only-ticks-over", "tick length millis").argumentUsage("timeout", "timeout seconds").argumentUsage("regex --thread", "thread regex").argumentUsage("combine-all", (String)null).argumentUsage("not-combined", (String)null).argumentUsage("force-java-sampler", (String)null).argumentUsage("stop --comment", "comment").argumentUsage("stop --order-by-time", (String)null).argumentUsage("stop --save-to-file", (String)null).executor(this::profiler).tabCompleter((platform, sender, arguments) -> {
         if (!arguments.contains("--info") && !arguments.contains("--cancel")) {
            if (!arguments.contains("--stop") && !arguments.contains("--upload")) {
               List<String> opts = new ArrayList(Arrays.asList("--info", "--stop", "--cancel", "--timeout", "--regex", "--combine-all", "--not-combined", "--interval", "--only-ticks-over", "--force-java-sampler"));
               opts.removeAll(arguments);
               opts.add("--thread");
               return (void)TabCompleter.create().from(0, CompletionSupplier.startsWith(opts)).complete(arguments);
            } else {
               return (void)TabCompleter.completeForOpts(arguments, "--order-by-time", "--comment", "--save-to-file");
            }
         } else {
            return (void)Collections.emptyList();
         }
      }).build());
   }

   private void profiler(SparkPlatform platform, CommandSender sender, CommandResponseHandler resp, Arguments arguments) {
      if (arguments.boolFlag("info")) {
         this.profilerInfo(resp);
      } else if (arguments.boolFlag("cancel")) {
         this.profilerCancel(resp);
      } else if (!arguments.boolFlag("stop") && !arguments.boolFlag("upload")) {
         this.profilerStart(platform, sender, resp, arguments);
      } else {
         this.profilerStop(platform, sender, resp, arguments);
      }
   }

   private void profilerStart(SparkPlatform platform, CommandSender sender, CommandResponseHandler resp, Arguments arguments) {
      int timeoutSeconds = arguments.intFlag("timeout");
      if (timeoutSeconds != -1 && timeoutSeconds <= 10) {
         resp.replyPrefixed(Component.text("The specified timeout is not long enough for accurate results to be formed. Please choose a value greater than 10.", NamedTextColor.RED));
      } else {
         if (timeoutSeconds != -1 && timeoutSeconds < 30) {
            resp.replyPrefixed(Component.text("The accuracy of the output will significantly improve when the profiler is able to run for longer periods. Consider setting a timeout value over 30 seconds."));
         }

         double intervalMillis = arguments.doubleFlag("interval");
         if (intervalMillis <= 0.0D) {
            intervalMillis = 4.0D;
         }

         boolean ignoreSleeping = arguments.boolFlag("ignore-sleeping");
         boolean ignoreNative = arguments.boolFlag("ignore-native");
         boolean forceJavaSampler = arguments.boolFlag("force-java-sampler");
         Set<String> threads = arguments.stringFlag("thread");
         ThreadDumper threadDumper;
         if (threads.isEmpty()) {
            threadDumper = platform.getPlugin().getDefaultThreadDumper();
         } else if (threads.contains("*")) {
            threadDumper = ThreadDumper.ALL;
         } else if (arguments.boolFlag("regex")) {
            threadDumper = new ThreadDumper.Regex(threads);
         } else {
            threadDumper = new ThreadDumper.Specific(threads);
         }

         ThreadGrouper threadGrouper;
         if (arguments.boolFlag("combine-all")) {
            threadGrouper = ThreadGrouper.AS_ONE;
         } else if (arguments.boolFlag("not-combined")) {
            threadGrouper = ThreadGrouper.BY_NAME;
         } else {
            threadGrouper = ThreadGrouper.BY_POOL;
         }

         int ticksOver = arguments.intFlag("only-ticks-over");
         TickHook tickHook = null;
         if (ticksOver != -1) {
            tickHook = platform.getTickHook();
            if (tickHook == null) {
               resp.replyPrefixed(Component.text("Tick counting is not supported!", NamedTextColor.RED));
               return;
            }
         }

         if (this.activeSampler != null) {
            resp.replyPrefixed(Component.text("An active profiler is already running."));
         } else {
            resp.broadcastPrefixed(Component.text("Initializing a new profiler, please wait..."));
            SamplerBuilder builder = new SamplerBuilder();
            builder.threadDumper(threadDumper);
            builder.threadGrouper(threadGrouper);
            if (timeoutSeconds != -1) {
               builder.completeAfter((long)timeoutSeconds, TimeUnit.SECONDS);
            }

            builder.samplingInterval(intervalMillis);
            builder.ignoreSleeping(ignoreSleeping);
            builder.ignoreNative(ignoreNative);
            builder.forceJavaSampler(forceJavaSampler);
            if (ticksOver != -1) {
               builder.ticksOver(ticksOver, tickHook);
            }

            Sampler sampler = this.activeSampler = builder.start(platform);
            resp.broadcastPrefixed(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text("Profiler now active!", NamedTextColor.GOLD))).append(Component.space())).append(Component.text("(" + (sampler instanceof AsyncSampler ? "async" : "built-in java") + ")", NamedTextColor.DARK_GRAY))).build());
            if (timeoutSeconds == -1) {
               resp.broadcastPrefixed(Component.text("Use '/" + platform.getPlugin().getCommandName() + " profiler --stop' to stop profiling and upload the results."));
            } else {
               resp.broadcastPrefixed(Component.text("The results will be automatically returned after the profiler has been running for " + timeoutSeconds + " seconds."));
            }

            CompletableFuture<Sampler> future = this.activeSampler.getFuture();
            future.whenCompleteAsync((s, throwable) -> {
               if (throwable != null) {
                  resp.broadcastPrefixed(Component.text("Profiler operation failed unexpectedly. Error: " + throwable.toString(), NamedTextColor.RED));
                  throwable.printStackTrace();
               }

            });
            future.whenCompleteAsync((s, throwable) -> {
               if (sampler == this.activeSampler) {
                  this.activeSampler = null;
               }

            });
            if (timeoutSeconds != -1) {
               ThreadNodeOrder threadOrder = arguments.boolFlag("order-by-time") ? ThreadNodeOrder.BY_TIME : ThreadNodeOrder.BY_NAME;
               String comment = (String)Iterables.getFirst(arguments.stringFlag("comment"), (Object)null);
               MethodDisambiguator methodDisambiguator = new MethodDisambiguator();
               MergeMode mergeMode = arguments.boolFlag("separate-parent-calls") ? MergeMode.separateParentCalls(methodDisambiguator) : MergeMode.sameMethod(methodDisambiguator);
               boolean saveToFile = arguments.boolFlag("save-to-file");
               future.thenAcceptAsync((s) -> {
                  resp.broadcastPrefixed(Component.text("The active profiler has completed! Uploading results..."));
                  this.handleUpload(platform, resp, s, threadOrder, comment, mergeMode, saveToFile);
               });
            }

         }
      }
   }

   private void profilerInfo(CommandResponseHandler resp) {
      if (this.activeSampler == null) {
         resp.replyPrefixed(Component.text("There isn't an active profiler running."));
      } else {
         long timeout = this.activeSampler.getEndTime();
         if (timeout == -1L) {
            resp.replyPrefixed(Component.text("There is an active profiler currently running, with no defined timeout."));
         } else {
            long timeoutDiff = (timeout - System.currentTimeMillis()) / 1000L;
            resp.replyPrefixed(Component.text("There is an active profiler currently running, due to timeout in " + timeoutDiff + " seconds."));
         }

         long runningTime = (System.currentTimeMillis() - this.activeSampler.getStartTime()) / 1000L;
         resp.replyPrefixed(Component.text("It has been profiling for " + runningTime + " seconds so far."));
      }

   }

   private void profilerCancel(CommandResponseHandler resp) {
      if (this.activeSampler == null) {
         resp.replyPrefixed(Component.text("There isn't an active profiler running."));
      } else {
         this.close();
         resp.broadcastPrefixed(Component.text("The active profiler has been cancelled.", NamedTextColor.GOLD));
      }

   }

   private void profilerStop(SparkPlatform platform, CommandSender sender, CommandResponseHandler resp, Arguments arguments) {
      if (this.activeSampler == null) {
         resp.replyPrefixed(Component.text("There isn't an active profiler running."));
      } else {
         this.activeSampler.stop();
         resp.broadcastPrefixed(Component.text("The active profiler has been stopped! Uploading results..."));
         ThreadNodeOrder threadOrder = arguments.boolFlag("order-by-time") ? ThreadNodeOrder.BY_TIME : ThreadNodeOrder.BY_NAME;
         String comment = (String)Iterables.getFirst(arguments.stringFlag("comment"), (Object)null);
         MethodDisambiguator methodDisambiguator = new MethodDisambiguator();
         MergeMode mergeMode = arguments.boolFlag("separate-parent-calls") ? MergeMode.separateParentCalls(methodDisambiguator) : MergeMode.sameMethod(methodDisambiguator);
         boolean saveToFile = arguments.boolFlag("save-to-file");
         this.handleUpload(platform, resp, this.activeSampler, threadOrder, comment, mergeMode, saveToFile);
         this.activeSampler = null;
      }

   }

   private void handleUpload(SparkPlatform platform, CommandResponseHandler resp, Sampler sampler, ThreadNodeOrder threadOrder, String comment, MergeMode mergeMode, boolean saveToFileFlag) {
      SparkProtos.SamplerData output = sampler.toProto(platform.getPlugin().getPlatformInfo(), resp.sender(), threadOrder, comment, mergeMode, platform.createClassSourceLookup());
      boolean saveToFile = false;
      if (saveToFileFlag) {
         saveToFile = true;
      } else {
         try {
            String key = platform.getBytebinClient().postContent((AbstractMessageLite)output, SPARK_SAMPLER_MEDIA_TYPE).key();
            String url = platform.getViewerUrl() + key;
            resp.broadcastPrefixed(Component.text("Profiler results:", NamedTextColor.GOLD));
            resp.broadcast(((TextComponent.Builder)((TextComponent.Builder)Component.text().content(url).color(NamedTextColor.GRAY)).clickEvent(ClickEvent.openUrl(url))).build());
            platform.getActivityLog().addToLog(Activity.urlActivity(resp.sender(), System.currentTimeMillis(), "Profiler", url));
         } catch (IOException var13) {
            resp.broadcastPrefixed(Component.text("An error occurred whilst uploading the results. Attempting to save to disk instead.", NamedTextColor.RED));
            var13.printStackTrace();
            saveToFile = true;
         }
      }

      if (saveToFile) {
         Path file = platform.resolveSaveFile("profile", "sparkprofile");

         try {
            Files.write(file, output.toByteArray(), new OpenOption[0]);
            resp.broadcastPrefixed(((TextComponent.Builder)((TextComponent.Builder)Component.text().content("Profile written to: ").color(NamedTextColor.GOLD)).append(Component.text(file.toString(), NamedTextColor.GRAY))).build());
            resp.broadcastPrefixed(Component.text("You can read the profile file using the viewer web-app - " + platform.getViewerUrl(), NamedTextColor.GRAY));
            platform.getActivityLog().addToLog(Activity.fileActivity(resp.sender(), System.currentTimeMillis(), "Profiler", file.toString()));
         } catch (IOException var12) {
            resp.broadcastPrefixed(Component.text("An error occurred whilst saving the data.", NamedTextColor.RED));
            var12.printStackTrace();
         }
      }

   }
}
