package me.lucko.spark.forge.plugin;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import javax.annotation.Nullable;
import me.lucko.spark.common.SparkPlatform;
import me.lucko.spark.common.SparkPlugin;
import me.lucko.spark.common.sampler.ThreadDumper;
import me.lucko.spark.forge.Forge1122CommandSender;
import me.lucko.spark.forge.Forge1122SparkMod;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Forge1122SparkPlugin implements SparkPlugin, ICommand {
   private final Forge1122SparkMod mod;
   private final Logger logger;
   protected final ScheduledExecutorService scheduler;
   protected final SparkPlatform platform;
   protected final ThreadDumper.GameThread threadDumper = new ThreadDumper.GameThread();

   protected Forge1122SparkPlugin(Forge1122SparkMod mod) {
      this.mod = mod;
      this.logger = LogManager.getLogger("spark");
      this.scheduler = Executors.newSingleThreadScheduledExecutor((r) -> {
         Thread thread = Executors.defaultThreadFactory().newThread(r);
         thread.setName("spark-forge-async-worker");
         thread.setDaemon(true);
         return thread;
      });
      this.platform = new SparkPlatform(this);
   }

   public void enable() {
      this.platform.enable();
   }

   public void disable() {
      this.platform.disable();
      this.scheduler.shutdown();
   }

   public abstract boolean hasPermission(ICommandSender var1, String var2);

   public String getVersion() {
      return this.mod.getVersion();
   }

   public Path getPluginDirectory() {
      return this.mod.getConfigDirectory();
   }

   public void executeAsync(Runnable task) {
      this.scheduler.execute(task);
   }

   public void log(Level level, String msg) {
      if (level == Level.INFO) {
         this.logger.info(msg);
      } else if (level == Level.WARNING) {
         this.logger.warn(msg);
      } else {
         if (level != Level.SEVERE) {
            throw new IllegalArgumentException(level.getName());
         }

         this.logger.error(msg);
      }

   }

   public ThreadDumper getDefaultThreadDumper() {
      return this.threadDumper.get();
   }

   public String getName() {
      return this.getCommandName();
   }

   public String getUsage(ICommandSender iCommandSender) {
      return "/" + this.getCommandName();
   }

   public List<String> getAliases() {
      return Collections.singletonList(this.getCommandName());
   }

   public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
      this.threadDumper.ensureSetup();
      this.platform.executeCommand(new Forge1122CommandSender(sender, this), args);
   }

   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos blockPos) {
      return this.platform.tabCompleteCommand(new Forge1122CommandSender(sender, this), args);
   }

   public boolean checkPermission(MinecraftServer minecraftServer, ICommandSender sender) {
      return this.platform.hasPermissionForAnyCommand(new Forge1122CommandSender(sender, this));
   }

   public boolean isUsernameIndex(String[] strings, int i) {
      return false;
   }

   public int compareTo(ICommand o) {
      return this.getCommandName().compareTo(o.getName());
   }
}
