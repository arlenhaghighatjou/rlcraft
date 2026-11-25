package me.lucko.spark.common.command.modules;

import com.google.common.base.Strings;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.command.Arguments;
import me.lucko.spark.common.command.Command;
import me.lucko.spark.common.command.CommandModule;
import me.lucko.spark.common.command.CommandResponseHandler;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.command.tabcomplete.TabCompleter;
import me.lucko.spark.common.monitor.cpu.CpuMonitor;
import me.lucko.spark.common.monitor.tick.TickStatistics;
import me.lucko.spark.common.util.FormatUtil;
import me.lucko.spark.common.util.RollingAverage;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.TextComponent;
import me.lucko.spark.lib.adventure.text.format.NamedTextColor;
import me.lucko.spark.lib.adventure.text.format.TextColor;
import me.lucko.spark.lib.adventure.text.format.TextDecoration;

public class HealthModule implements CommandModule {
   private static final double MSPT_95_PERCENTILE = 0.95D;

   public HealthModule() {
   }

   public void registerCommands(Consumer<Command> consumer) {
      consumer.accept(Command.builder().aliases("tps", "cpu").executor(HealthModule::tps).tabCompleter(Command.TabCompleter.empty()).build());
      consumer.accept(Command.builder().aliases("healthreport", "health", "ht").argumentUsage("memory", (String)null).executor(HealthModule::healthReport).tabCompleter((platform, sender, arguments) -> {
         return (void)TabCompleter.completeForOpts(arguments, "--memory");
      }).build());
   }

   private static void tps(SparkPlatform platform, CommandSender sender, CommandResponseHandler resp, Arguments arguments) {
      TickStatistics tickStatistics = platform.getTickStatistics();
      if (tickStatistics != null) {
         resp.replyPrefixed(Component.text("TPS from last 5s, 10s, 1m, 5m, 15m:"));
         resp.replyPrefixed(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content(" ").append(formatTps(tickStatistics.tps5Sec()))).append(Component.text(", "))).append(formatTps(tickStatistics.tps10Sec()))).append(Component.text(", "))).append(formatTps(tickStatistics.tps1Min()))).append(Component.text(", "))).append(formatTps(tickStatistics.tps5Min()))).append(Component.text(", "))).append(formatTps(tickStatistics.tps15Min()))).build());
         resp.replyPrefixed(Component.empty());
         if (tickStatistics.isDurationSupported()) {
            resp.replyPrefixed(Component.text("Tick durations (min/med/95%ile/max ms) from last 10s, 1m:"));
            resp.replyPrefixed(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content(" ").append(formatTickDurations(tickStatistics.duration10Sec()))).append(Component.text(";  "))).append(formatTickDurations(tickStatistics.duration1Min()))).build());
            resp.replyPrefixed(Component.empty());
         }
      }

      resp.replyPrefixed(Component.text("CPU usage from last 10s, 1m, 15m:"));
      resp.replyPrefixed(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content(" ").append(formatCpuUsage(CpuMonitor.systemLoad10SecAvg()))).append(Component.text(", "))).append(formatCpuUsage(CpuMonitor.systemLoad1MinAvg()))).append(Component.text(", "))).append(formatCpuUsage(CpuMonitor.systemLoad15MinAvg()))).append(Component.text("  (system)", NamedTextColor.DARK_GRAY))).build());
      resp.replyPrefixed(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content(" ").append(formatCpuUsage(CpuMonitor.processLoad10SecAvg()))).append(Component.text(", "))).append(formatCpuUsage(CpuMonitor.processLoad1MinAvg()))).append(Component.text(", "))).append(formatCpuUsage(CpuMonitor.processLoad15MinAvg()))).append(Component.text("  (process)", NamedTextColor.DARK_GRAY))).build());
   }

   private static void healthReport(SparkPlatform platform, CommandSender sender, CommandResponseHandler resp, Arguments arguments) {
      resp.replyPrefixed(Component.text("Generating server health report..."));
      List<Component> report = new LinkedList();
      report.add(Component.empty());
      TickStatistics tickStatistics = platform.getTickStatistics();
      if (tickStatistics != null) {
         addTickStats(report, tickStatistics);
      }

      addCpuStats(report);
      MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
      addBasicMemoryStats(report, memoryMXBean);
      if (arguments.boolFlag("memory")) {
         addDetailedMemoryStats(report, memoryMXBean);
      }

      try {
         addDiskStats(report);
      } catch (IOException var8) {
         var8.printStackTrace();
      }

      Objects.requireNonNull(resp);
      report.forEach(resp::reply);
   }

   private static void addTickStats(List<Component> report, TickStatistics tickStatistics) {
      report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text(">", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))).append(Component.space())).append(Component.text("TPS from last 5s, 10s, 1m, 5m, 15m:", NamedTextColor.GOLD))).build());
      report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content("    ").append(formatTps(tickStatistics.tps5Sec()))).append(Component.text(", "))).append(formatTps(tickStatistics.tps10Sec()))).append(Component.text(", "))).append(formatTps(tickStatistics.tps1Min()))).append(Component.text(", "))).append(formatTps(tickStatistics.tps5Min()))).append(Component.text(", "))).append(formatTps(tickStatistics.tps15Min()))).build());
      report.add(Component.empty());
      if (tickStatistics.isDurationSupported()) {
         report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text(">", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))).append(Component.space())).append(Component.text("Tick durations (min/med/95%ile/max ms) from last 10s, 1m:", NamedTextColor.GOLD))).build());
         report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content("    ").append(formatTickDurations(tickStatistics.duration10Sec()))).append(Component.text("; "))).append(formatTickDurations(tickStatistics.duration1Min()))).build());
         report.add(Component.empty());
      }

   }

   private static void addCpuStats(List<Component> report) {
      report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text(">", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))).append(Component.space())).append(Component.text("CPU usage from last 10s, 1m, 15m:", NamedTextColor.GOLD))).build());
      report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content("    ").append(formatCpuUsage(CpuMonitor.systemLoad10SecAvg()))).append(Component.text(", "))).append(formatCpuUsage(CpuMonitor.systemLoad1MinAvg()))).append(Component.text(", "))).append(formatCpuUsage(CpuMonitor.systemLoad15MinAvg()))).append(Component.text("  (system)", NamedTextColor.DARK_GRAY))).build());
      report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content("    ").append(formatCpuUsage(CpuMonitor.processLoad10SecAvg()))).append(Component.text(", "))).append(formatCpuUsage(CpuMonitor.processLoad1MinAvg()))).append(Component.text(", "))).append(formatCpuUsage(CpuMonitor.processLoad15MinAvg()))).append(Component.text("  (process)", NamedTextColor.DARK_GRAY))).build());
      report.add(Component.empty());
   }

   private static void addBasicMemoryStats(List<Component> report, MemoryMXBean memoryMXBean) {
      MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
      report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text(">", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))).append(Component.space())).append(Component.text("Memory usage:", NamedTextColor.GOLD))).build());
      report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content("    ").append(Component.text(FormatUtil.formatBytes(heapUsage.getUsed()), NamedTextColor.WHITE))).append(Component.space())).append(Component.text("/", NamedTextColor.GRAY))).append(Component.space())).append(Component.text(FormatUtil.formatBytes(heapUsage.getMax()), NamedTextColor.WHITE))).append(Component.text("   "))).append(Component.text("(", NamedTextColor.GRAY))).append(Component.text(FormatUtil.percent((double)heapUsage.getUsed(), (double)heapUsage.getMax()), NamedTextColor.GREEN))).append(Component.text(")", NamedTextColor.GRAY))).build());
      report.add(((TextComponent.Builder)Component.text().content("    ").append(generateMemoryUsageDiagram(heapUsage, 40))).build());
      report.add(Component.empty());
   }

   private static void addDetailedMemoryStats(List<Component> report, MemoryMXBean memoryMXBean) {
      MemoryUsage nonHeapUsage = memoryMXBean.getNonHeapMemoryUsage();
      report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text(">", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))).append(Component.space())).append(Component.text("Non-heap memory usage:", NamedTextColor.GOLD))).build());
      report.add(((TextComponent.Builder)Component.text().content("    ").append(Component.text(FormatUtil.formatBytes(nonHeapUsage.getUsed()), NamedTextColor.WHITE))).build());
      report.add(Component.empty());

      for(MemoryPoolMXBean memoryPool : ManagementFactory.getMemoryPoolMXBeans()) {
         if (memoryPool.getType() == MemoryType.HEAP) {
            MemoryUsage usage = memoryPool.getUsage();
            MemoryUsage collectionUsage = memoryPool.getCollectionUsage();
            if (usage.getMax() == -1L) {
               usage = new MemoryUsage(usage.getInit(), usage.getUsed(), usage.getCommitted(), usage.getCommitted());
            }

            report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text(">", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))).append(Component.space())).append(Component.text(memoryPool.getName() + " pool usage:", NamedTextColor.GOLD))).build());
            report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content("    ").append(Component.text(FormatUtil.formatBytes(usage.getUsed()), NamedTextColor.WHITE))).append(Component.space())).append(Component.text("/", NamedTextColor.GRAY))).append(Component.space())).append(Component.text(FormatUtil.formatBytes(usage.getMax()), NamedTextColor.WHITE))).append(Component.text("   "))).append(Component.text("(", NamedTextColor.GRAY))).append(Component.text(FormatUtil.percent((double)usage.getUsed(), (double)usage.getMax()), NamedTextColor.GREEN))).append(Component.text(")", NamedTextColor.GRAY))).build());
            report.add(((TextComponent.Builder)Component.text().content("    ").append(generateMemoryPoolDiagram(usage, collectionUsage, 40))).build());
            if (collectionUsage != null) {
               report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content("     ").append(Component.text("-", NamedTextColor.RED))).append(Component.space())).append(Component.text("Usage at last GC:", NamedTextColor.GRAY))).append(Component.space())).append(Component.text(FormatUtil.formatBytes(collectionUsage.getUsed()), NamedTextColor.WHITE))).build());
            }

            report.add(Component.empty());
         }
      }

   }

   private static void addDiskStats(List<Component> report) throws IOException {
      FileStore fileStore = Files.getFileStore(Paths.get("."));
      long totalSpace = fileStore.getTotalSpace();
      long usedSpace = totalSpace - fileStore.getUsableSpace();
      report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text(">", NamedTextColor.DARK_GRAY, TextDecoration.BOLD))).append(Component.space())).append(Component.text("Disk usage:", NamedTextColor.GOLD))).build());
      report.add(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content("    ").append(Component.text(FormatUtil.formatBytes(usedSpace), NamedTextColor.WHITE))).append(Component.space())).append(Component.text("/", NamedTextColor.GRAY))).append(Component.space())).append(Component.text(FormatUtil.formatBytes(totalSpace), NamedTextColor.WHITE))).append(Component.text("   "))).append(Component.text("(", NamedTextColor.GRAY))).append(Component.text(FormatUtil.percent((double)usedSpace, (double)totalSpace), NamedTextColor.GREEN))).append(Component.text(")", NamedTextColor.GRAY))).build());
      report.add(((TextComponent.Builder)Component.text().content("    ").append(generateDiskUsageDiagram((double)usedSpace, (double)totalSpace, 40))).build());
      report.add(Component.empty());
   }

   public static TextComponent formatTps(double tps) {
      TextColor color;
      if (tps > 18.0D) {
         color = NamedTextColor.GREEN;
      } else if (tps > 16.0D) {
         color = NamedTextColor.YELLOW;
      } else {
         color = NamedTextColor.RED;
      }

      return Component.text((tps > 20.0D ? "*" : "") + Math.min((double)Math.round(tps * 100.0D) / 100.0D, 20.0D), color);
   }

   public static TextComponent formatTickDurations(RollingAverage average) {
      return (TextComponent)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(formatTickDuration(average.min()))).append(Component.text('/', NamedTextColor.GRAY))).append(formatTickDuration(average.median()))).append(Component.text('/', NamedTextColor.GRAY))).append(formatTickDuration(average.percentile(0.95D)))).append(Component.text('/', NamedTextColor.GRAY))).append(formatTickDuration(average.max()))).build();
   }

   public static TextComponent formatTickDuration(double duration) {
      TextColor color;
      if (duration >= 50.0D) {
         color = NamedTextColor.RED;
      } else if (duration >= 40.0D) {
         color = NamedTextColor.YELLOW;
      } else {
         color = NamedTextColor.GREEN;
      }

      return Component.text(String.format("%.1f", duration), color);
   }

   public static TextComponent formatCpuUsage(double usage) {
      TextColor color;
      if (usage > 0.9D) {
         color = NamedTextColor.RED;
      } else if (usage > 0.65D) {
         color = NamedTextColor.YELLOW;
      } else {
         color = NamedTextColor.GREEN;
      }

      return Component.text(FormatUtil.percent(usage, 1.0D), color);
   }

   private static TextComponent generateMemoryUsageDiagram(MemoryUsage usage, int length) {
      double used = (double)usage.getUsed();
      double committed = (double)usage.getCommitted();
      double max = (double)usage.getMax();
      int usedChars = (int)(used * (double)length / max);
      int committedChars = (int)(committed * (double)length / max);
      TextComponent.Builder line = (TextComponent.Builder)Component.text().content(Strings.repeat("/", usedChars)).color(NamedTextColor.GRAY);
      if (committedChars > usedChars) {
         line.append(Component.text(Strings.repeat(" ", committedChars - usedChars - 1)));
         line.append(Component.text("|", NamedTextColor.YELLOW));
      }

      if (length > committedChars) {
         line.append(Component.text(Strings.repeat(" ", length - committedChars)));
      }

      return (TextComponent)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text("[", NamedTextColor.DARK_GRAY))).append(line.build())).append(Component.text("]", NamedTextColor.DARK_GRAY))).build();
   }

   private static TextComponent generateMemoryPoolDiagram(MemoryUsage usage, MemoryUsage collectionUsage, int length) {
      double used = (double)usage.getUsed();
      double collectionUsed = used;
      if (collectionUsage != null) {
         collectionUsed = (double)collectionUsage.getUsed();
      }

      double committed = (double)usage.getCommitted();
      double max = (double)usage.getMax();
      int usedChars = (int)(used * (double)length / max);
      int collectionUsedChars = (int)(collectionUsed * (double)length / max);
      int committedChars = (int)(committed * (double)length / max);
      TextComponent.Builder line = (TextComponent.Builder)Component.text().content(Strings.repeat("/", collectionUsedChars)).color(NamedTextColor.GRAY);
      if (usedChars > collectionUsedChars) {
         line.append(Component.text("|", NamedTextColor.RED));
         line.append(Component.text(Strings.repeat("/", usedChars - collectionUsedChars - 1), NamedTextColor.GRAY));
      }

      if (committedChars > usedChars) {
         line.append(Component.text(Strings.repeat(" ", committedChars - usedChars - 1)));
         line.append(Component.text("|", NamedTextColor.YELLOW));
      }

      if (length > committedChars) {
         line.append(Component.text(Strings.repeat(" ", length - committedChars)));
      }

      return (TextComponent)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text("[", NamedTextColor.DARK_GRAY))).append(line.build())).append(Component.text("]", NamedTextColor.DARK_GRAY))).build();
   }

   private static TextComponent generateDiskUsageDiagram(double used, double max, int length) {
      int usedChars = (int)(used * (double)length / max);
      String line = Strings.repeat("/", usedChars) + Strings.repeat(" ", length - usedChars);
      return (TextComponent)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text("[", NamedTextColor.DARK_GRAY))).append(Component.text(line, NamedTextColor.GRAY))).append(Component.text("]", NamedTextColor.DARK_GRAY))).build();
   }
}
