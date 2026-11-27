package materialtweaker.handlers;

import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

@Config(
   modid = "materialtweaker"
)
public class ForgeConfigHandler {
   @Comment({"Server Config"})
   @Name("Server")
   public static final ForgeConfigHandler.ServerConfig server = new ForgeConfigHandler.ServerConfig();

   public ForgeConfigHandler() {
   }

   public static class EventHandler {
      private EventHandler() {
      }

      public static void register(EventBus bus) {
         bus.register(OnConfigChangedEvent.class, EventHandler::onConfigChanged);
      }

      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("materialtweaker")) {
            ConfigManager.sync("materialtweaker", Type.INSTANCE);
         }

      }
   }

   public static class ServerConfig {
      @Comment({"Print information about material attributes when they are registered."})
      @Name("Print Material Info")
      public boolean printInfo = false;

      public ServerConfig() {
      }
   }

   public static void register() {
      ConfigManager.register("materialtweaker", "", "general", null,
         ConfigEntry.category("Server",
            ConfigEntry.value("Print Material Info", boolean.class, () -> ForgeConfigHandler.server.printInfo, v -> ForgeConfigHandler.server.printInfo = (Boolean)v).comment("Print information about material attributes when they are registered.").langKey("materialtweaker.general.server.printinfo")).comment("Server Config").langKey("materialtweaker.general.server"));
   }
}
