package me.lucko.spark.forge.plugin;

import java.util.stream.Stream;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.tick.TickReporter;
import me.lucko.spark.forge.Forge1122CommandSender;
import me.lucko.spark.forge.Forge1122PlatformInfo;
import me.lucko.spark.forge.Forge1122SparkMod;
import me.lucko.spark.forge.Forge1122TickHook;
import me.lucko.spark.forge.Forge1122TickReporter;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;

public class Forge1122ClientSparkPlugin extends Forge1122SparkPlugin {
   private final Minecraft minecraft;

   public static void register(Forge1122SparkMod mod) {
      Forge1122ClientSparkPlugin plugin = new Forge1122ClientSparkPlugin(mod, Minecraft.getMinecraft());
      plugin.enable();
      MinecraftForge.EVENT_BUS.register(plugin);
      ClientCommandHandler.instance.registerCommand(plugin);
   }

   public Forge1122ClientSparkPlugin(Forge1122SparkMod mod, Minecraft minecraft) {
      super(mod);
      this.minecraft = minecraft;
   }

   public boolean hasPermission(ICommandSender sender, String permission) {
      return true;
   }

   public Stream<Forge1122CommandSender> getCommandSenders() {
      return Stream.of(new Forge1122CommandSender(this.minecraft.player, this));
   }

   public TickHook createTickHook() {
      return new Forge1122TickHook(Type.CLIENT);
   }

   public TickReporter createTickReporter() {
      return new Forge1122TickReporter(Type.CLIENT);
   }

   public PlatformInfo getPlatformInfo() {
      return new Forge1122PlatformInfo(PlatformInfo.Type.CLIENT);
   }

   public String getCommandName() {
      return "sparkc";
   }
}
