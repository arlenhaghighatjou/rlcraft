package fonnymunkey.portaldupebegone;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(
   modid = "portaldupebegone"
)
public class ModConfig {
   @Comment({"Server Config"})
   @Name("Server")
   public static final ModConfig.ServerConfig server = new ModConfig.ServerConfig();

   public ModConfig() {
   }

   @EventBusSubscriber(
      modid = "portaldupebegone"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
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
}
