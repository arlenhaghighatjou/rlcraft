package materialtweaker.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(
   modid = "materialtweaker"
)
public class ForgeConfigHandler {
   @Comment({"Server Config"})
   @Name("Server")
   public static final ForgeConfigHandler.ServerConfig server = new ForgeConfigHandler.ServerConfig();

   public ForgeConfigHandler() {
   }

   @EventBusSubscriber(
      modid = "materialtweaker"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
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
}
