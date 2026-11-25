package fonnymunkey.portaldupebegone;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

@Config(
   modid = "portaldupebegone"
)
public class ModConfig {
   @Comment({"Server Config"})
   @Name("Server")
   public static final ModConfig.ServerConfig server = new ModConfig.ServerConfig();

   public ModConfig() {
   }

   public static class EventHandler {
      private EventHandler() {
      }

      public static void register(EventBus bus) {
         bus.register(OnConfigChangedEvent.class, EventHandler::onConfigChanged);
      }

      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("portaldupebegone")) {
            ConfigManager.sync("portaldupebegone", Type.INSTANCE);
         }

      }
   }

   public static class ServerConfig {
      @Comment({"Log possible attempted dupes with xyz coordinates and entity name. (May cause some log spam when it happens.)"})
      @Name("Log Possible Attempts")
      public boolean logPossibleAttemptedDupe = false;

      public ServerConfig() {
      }
   }

   public static void register() {
      ConfigManager.register("portaldupebegone", "", "general", null,
         ConfigEntry.category("Server",
            ConfigEntry.value("Log Possible Attempts", boolean.class, () -> ModConfig.server.logPossibleAttemptedDupe, v -> ModConfig.server.logPossibleAttemptedDupe = (Boolean)v).comment("Log possible attempted dupes with xyz coordinates and entity name. (May cause some log spam when it happens.)").langKey("portaldupebegone.general.server.logpossibleattempteddupe")).comment("Server Config").langKey("portaldupebegone.general.server"));
   }
}
