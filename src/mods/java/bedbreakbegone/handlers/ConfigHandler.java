package bedbreakbegone.handlers;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

@Config(
   modid = "bedbreakbegone"
)
public class ConfigHandler {
   @Comment({"Server Config"})
   @Name("Server")
   public static final ConfigHandler.ServerConfig server = new ConfigHandler.ServerConfig();

   public ConfigHandler() {
   }

   public static class EventHandler {
      private EventHandler() {
      }

      public static void register(EventBus bus) {
         bus.register(OnConfigChangedEvent.class, EventHandler::onConfigChanged);
      }

      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("bedbreakbegone")) {
            ConfigManager.sync("bedbreakbegone", Type.INSTANCE);
         }

      }
   }

   public static class ServerConfig {
      @Comment({"Output a warning and coordinates in the log when a spawnpoint fails?"})
      @Name("Failed Spawn Warning")
      public boolean spawnWarning = false;

      public ServerConfig() {
      }
   }

   public static void register() {
      ConfigManager.register("bedbreakbegone", "", "general", null,
         ConfigEntry.category("Server",
            ConfigEntry.value("Failed Spawn Warning", boolean.class, () -> ConfigHandler.server.spawnWarning, v -> ConfigHandler.server.spawnWarning = (Boolean)v).comment("Output a warning and coordinates in the log when a spawnpoint fails?").langKey("bedbreakbegone.general.server.spawnwarning")).comment("Server Config").langKey("bedbreakbegone.general.server"));
   }
}
