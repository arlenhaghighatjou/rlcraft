package me.lucko.spark.forge.plugin;

import java.util.function.Function;
import java.util.stream.Stream;
import me.lucko.spark.common.platform.PlatformInfo;
import me.lucko.spark.common.tick.TickHook;
import me.lucko.spark.common.tick.TickReporter;
import me.lucko.spark.forge.Forge1122CommandSender;
import me.lucko.spark.forge.Forge1122PlatformInfo;
import me.lucko.spark.forge.Forge1122SparkMod;
import me.lucko.spark.forge.Forge1122TickHook;
import me.lucko.spark.forge.Forge1122TickReporter;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public class Forge1122ServerSparkPlugin extends Forge1122SparkPlugin {
   private final MinecraftServer server;

   public static Forge1122ServerSparkPlugin register(Forge1122SparkMod mod, FMLServerStartingEvent event) {
      Forge1122ServerSparkPlugin plugin = new Forge1122ServerSparkPlugin(mod, event.getServer());
      plugin.enable();
      event.registerServerCommand(plugin);
      PermissionAPI.registerNode("spark", DefaultPermissionLevel.OP, "Access to the spark command");
      return plugin;
   }

   public Forge1122ServerSparkPlugin(Forge1122SparkMod mod, MinecraftServer server) {
      super(mod);
      this.server = server;
   }

   public boolean hasPermission(ICommandSender sender, String permission) {
      return sender instanceof EntityPlayer ? PermissionAPI.hasPermission((EntityPlayer)sender, permission) : true;
   }

   public Stream<Forge1122CommandSender> getCommandSenders() {
      return Stream.concat(this.server.getPlayerList().getPlayers().stream(), Stream.of(this.server)).map((sender) -> {
         return new Forge1122CommandSender(sender, this);
      });
   }

   public TickHook createTickHook() {
      return new Forge1122TickHook(Type.SERVER);
   }

   public TickReporter createTickReporter() {
      return new Forge1122TickReporter(Type.SERVER);
   }

   public PlatformInfo getPlatformInfo() {
      return new Forge1122PlatformInfo(PlatformInfo.Type.SERVER);
   }

   public String getCommandName() {
      return "spark";
   }
}
