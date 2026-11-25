package me.lucko.spark.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList.Builder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.lucko.spark.common.activitylog.ActivityLog;
import me.lucko.spark.common.api.SparkApi;
import me.lucko.spark.common.command.Arguments;
import me.lucko.spark.common.command.Command;
import me.lucko.spark.common.command.CommandModule;
import me.lucko.spark.common.command.CommandResponseHandler;
import me.lucko.spark.common.command.modules.ActivityLogModule;
import me.lucko.spark.common.command.modules.GcMonitoringModule;
import me.lucko.spark.common.command.modules.HealthModule;
import me.lucko.spark.common.command.modules.HeapAnalysisModule;
import me.lucko.spark.common.command.modules.SamplerModule;
import me.lucko.spark.common.command.modules.TickMonitoringModule;
import me.lucko.spark.common.command.sender.CommandSender;
import me.lucko.spark.common.command.tabcomplete.CompletionSupplier;
import me.lucko.spark.common.command.tabcomplete.TabCompleter;
import me.lucko.spark.common.monitor.cpu.CpuMonitor;
import me.lucko.spark.common.monitor.memory.GarbageCollectorStatistics;
import me.lucko.spark.common.monitor.tick.TickStatistics;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.tick.TickReporter;
import me.lucko.spark.common.util.BytebinClient;
import me.lucko.spark.common.util.ClassSourceLookup;
import me.lucko.spark.common.util.Configuration;
import me.lucko.spark.common.util.TemporaryFiles;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.TextComponent;
import me.lucko.spark.lib.adventure.text.event.ClickEvent;
import me.lucko.spark.lib.adventure.text.format.NamedTextColor;
import me.lucko.spark.lib.adventure.text.format.TextDecoration;
import me.lucko.spark.lib.okhttp3.OkHttpClient;

public class SparkPlatform {
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss");
   private final SparkPlugin plugin;
   private final Configuration configuration;
   private final String viewerUrl;
   private final OkHttpClient httpClient;
   private final BytebinClient bytebinClient;
   private final List<CommandModule> commandModules;
   private final List<Command> commands;
   private final ReentrantLock commandExecuteLock = new ReentrantLock(true);
   private final ActivityLog activityLog;
   private final TickHook tickHook;
   private final TickReporter tickReporter;
   private final TickStatistics tickStatistics;
   private Map<String, GarbageCollectorStatistics> startupGcStatistics = ImmutableMap.of();
   private long serverNormalOperationStartTime;
   private final AtomicBoolean enabled = new AtomicBoolean(false);

   public SparkPlatform(SparkPlugin plugin) {
      this.plugin = plugin;
      this.configuration = new Configuration(this.plugin.getPluginDirectory().resolve("config.json"));
      this.viewerUrl = this.configuration.getString("viewerUrl", "https://spark.lucko.me/");
      String bytebinUrl = this.configuration.getString("bytebinUrl", "https://bytebin.lucko.me/");
      this.httpClient = new OkHttpClient();
      this.bytebinClient = new BytebinClient(this.httpClient, bytebinUrl, "spark-plugin");
      this.commandModules = ImmutableList.of(new SamplerModule(), new HealthModule(), new TickMonitoringModule(), new GcMonitoringModule(), new HeapAnalysisModule(), new ActivityLogModule());
      Builder<Command> commandsBuilder = ImmutableList.builder();

      for(CommandModule module : this.commandModules) {
         Objects.requireNonNull(commandsBuilder);
         module.registerCommands(commandsBuilder::add);
      }

      this.commands = commandsBuilder.build();
      this.activityLog = new ActivityLog(plugin.getPluginDirectory().resolve("activity.json"));
      this.activityLog.load();
      this.tickHook = plugin.createTickHook();
      this.tickReporter = plugin.createTickReporter();
      this.tickStatistics = this.tickHook != null ? new TickStatistics() : null;
   }

   public void enable() {
      if (!this.enabled.compareAndSet(false, true)) {
         throw new RuntimeException("Platform has already been enabled!");
      } else {
         if (this.tickHook != null) {
            this.tickHook.addCallback(this.tickStatistics);
            this.tickHook.start();
         }

         if (this.tickReporter != null) {
            this.tickReporter.addCallback(this.tickStatistics);
            this.tickReporter.start();
         }

         CpuMonitor.ensureMonitoring();
         this.plugin.executeAsync(() -> {
            this.startupGcStatistics = GarbageCollectorStatistics.pollStats();
            this.serverNormalOperationStartTime = System.currentTimeMillis();
         });
         SparkApi api = new SparkApi(this);
         this.plugin.registerApi(api);
         SparkApi.register(api);
      }
   }

   public void disable() {
      if (this.tickHook != null) {
         this.tickHook.close();
      }

      if (this.tickReporter != null) {
         this.tickReporter.close();
      }

      for(CommandModule module : this.commandModules) {
         module.close();
      }

      SparkApi.unregister();
      TemporaryFiles.deleteTemporaryFiles();
      this.httpClient.dispatcher().executorService().shutdown();
      this.httpClient.connectionPool().evictAll();
   }

   public SparkPlugin getPlugin() {
      return this.plugin;
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public String getViewerUrl() {
      return this.viewerUrl;
   }

   public BytebinClient getBytebinClient() {
      return this.bytebinClient;
   }

   public ActivityLog getActivityLog() {
      return this.activityLog;
   }

   public TickHook getTickHook() {
      return this.tickHook;
   }

   public TickReporter getTickReporter() {
      return this.tickReporter;
   }

   public ClassSourceLookup createClassSourceLookup() {
      return this.plugin.createClassSourceLookup();
   }

   public TickStatistics getTickStatistics() {
      return this.tickStatistics;
   }

   public Map<String, GarbageCollectorStatistics> getStartupGcStatistics() {
      return this.startupGcStatistics;
   }

   public long getServerNormalOperationStartTime() {
      return this.serverNormalOperationStartTime;
   }

   public Path resolveSaveFile(String prefix, String extension) {
      Path pluginFolder = this.plugin.getPluginDirectory();

      try {
         Files.createDirectories(pluginFolder);
      } catch (IOException var5) {
         ;
      }

      return pluginFolder.resolve(prefix + "-" + DATE_TIME_FORMATTER.format(LocalDateTime.now()) + "." + extension);
   }

   private List<Command> getAvailableCommands(CommandSender sender) {
      return sender.hasPermission("spark") ? this.commands : (List)this.commands.stream().filter((c) -> {
         return sender.hasPermission("spark." + c.primaryAlias());
      }).collect(Collectors.toList());
   }

   public boolean hasPermissionForAnyCommand(CommandSender sender) {
      return !this.getAvailableCommands(sender).isEmpty();
   }

   public void executeCommand(CommandSender sender, String[] args) {
      this.plugin.executeAsync(() -> {
         this.commandExecuteLock.lock();

         try {
            this.executeCommand0(sender, args);
         } finally {
            this.commandExecuteLock.unlock();
         }

      });
   }

   private void executeCommand0(CommandSender sender, String[] args) {
      CommandResponseHandler resp = new CommandResponseHandler(this, sender);
      List<Command> commands = this.getAvailableCommands(sender);
      if (commands.isEmpty()) {
         resp.replyPrefixed(Component.text("You do not have permission to use this command.", NamedTextColor.RED));
      } else if (args.length == 0) {
         resp.replyPrefixed(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text("spark", NamedTextColor.WHITE))).append(Component.space())).append(Component.text("v" + this.getPlugin().getVersion(), NamedTextColor.GRAY))).build());
         resp.replyPrefixed(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().color(NamedTextColor.GRAY)).append(Component.text("Use "))).append(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content("/" + this.getPlugin().getCommandName() + " help").color(NamedTextColor.WHITE)).decoration(TextDecoration.UNDERLINED, true)).clickEvent(ClickEvent.runCommand("/" + this.getPlugin().getCommandName() + " help"))).build())).append(Component.text(" to view usage information."))).build());
      } else {
         ArrayList<String> rawArgs = new ArrayList(Arrays.asList(args));
         String alias = ((String)rawArgs.remove(0)).toLowerCase();

         for(Command command : commands) {
            if (command.aliases().contains(alias)) {
               resp.setCommandPrimaryAlias(command.primaryAlias());

               try {
                  command.executor().execute(this, sender, resp, new Arguments(rawArgs));
               } catch (Arguments.ParseException var10) {
                  resp.replyPrefixed(Component.text(var10.getMessage(), NamedTextColor.RED));
               }

               return;
            }
         }

         this.sendUsage(commands, resp);
      }
   }

   public List<String> tabCompleteCommand(CommandSender sender, String[] args) {
      List<Command> commands = this.getAvailableCommands(sender);
      if (commands.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<String> arguments = new ArrayList(Arrays.asList(args));
         if (args.length <= 1) {
            List<String> mainCommands = (List)commands.stream().map(Command::primaryAlias).collect(Collectors.toList());
            return TabCompleter.create().at(0, CompletionSupplier.startsWith(mainCommands)).complete(arguments);
         } else {
            String alias = (String)arguments.remove(0);

            for(Command command : commands) {
               if (command.aliases().contains(alias)) {
                  return command.tabCompleter().completions(this, sender, arguments);
               }
            }

            return Collections.emptyList();
         }
      }
   }

   private void sendUsage(List<Command> commands, CommandResponseHandler sender) {
      sender.replyPrefixed(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text("spark", NamedTextColor.WHITE))).append(Component.space())).append(Component.text("v" + this.getPlugin().getVersion(), NamedTextColor.GRAY))).build());

      for(Command command : commands) {
         String usage = "/" + this.getPlugin().getCommandName() + " " + command.primaryAlias();
         ClickEvent clickEvent = ClickEvent.suggestCommand(usage);
         sender.reply(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().append(Component.text(">", NamedTextColor.GOLD, TextDecoration.BOLD))).append(Component.space())).append(((TextComponent.Builder)((TextComponent.Builder)Component.text().content(usage).color(NamedTextColor.GRAY)).clickEvent(clickEvent)).build())).build());

         for(Command.ArgumentInfo arg : command.arguments()) {
            if (arg.requiresParameter()) {
               sender.reply(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content("       ").append(Component.text("[", NamedTextColor.DARK_GRAY))).append(Component.text("--" + arg.argumentName(), NamedTextColor.GRAY))).append(Component.space())).append(Component.text("<" + arg.parameterDescription() + ">", NamedTextColor.DARK_GRAY))).append(Component.text("]", NamedTextColor.DARK_GRAY))).build());
            } else {
               sender.reply(((TextComponent.Builder)((TextComponent.Builder)((TextComponent.Builder)Component.text().content("       ").append(Component.text("[", NamedTextColor.DARK_GRAY))).append(Component.text("--" + arg.argumentName(), NamedTextColor.GRAY))).append(Component.text("]", NamedTextColor.DARK_GRAY))).build());
            }
         }
      }

   }
}
