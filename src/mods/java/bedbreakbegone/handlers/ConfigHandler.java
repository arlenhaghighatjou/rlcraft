package bedbreakbegone.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(
   modid = "bedbreakbegone"
)
public class ConfigHandler {
   @Comment({"Server Config"})
   @Name("Server")
   public static final ConfigHandler.ServerConfig server = new ConfigHandler.ServerConfig();

   public ConfigHandler() {
   }

   @EventBusSubscriber(
      modid = "bedbreakbegone"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
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
}
