package bqtweaker.handlers;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(
   modid = "bqtweaker"
)
public class ConfigHandler {
   @Comment({"Client Config"})
   @Name("Client")
   public static final ConfigHandler.ClientConfig client = new ConfigHandler.ClientConfig();

   public ConfigHandler() {
   }

   public static class ClientConfig {
      @Comment({"Should the button for toggling notifications be enabled?"})
      @Name("BQ Notifications Button")
      public boolean bqNotifEnabled = true;
      @Comment({"Should BQTweaker attempt to patch an entity rendering bug when Mobends is active? (Disabling this disables Party Create and Party Manage overrides as well.)"})
      @Name("BQ Mobends Patch")
      public boolean bqMobendPatch = true;
      @Comment({"Override BQ's Home page?"})
      @Name("BQ Home Override")
      public boolean bqHomeOverride = true;
      @Comment({"Override BQ's Party Create page?"})
      @Name("BQ Party Create Override")
      public boolean bqPartyCreateOverride = true;
      @Comment({"Override BQ's Party Manage page?"})
      @Name("BQ Party Manage Override")
      public boolean bqPartyManageOverride = true;
      @Comment({"Override BQ's Quest page?"})
      @Name("BQ Quest Override")
      public boolean bqQuestOverride = true;
      @Comment({"Override BQ's Questline page?"})
      @Name("BQ Questline Override")
      public boolean bqQuestlineOverride = true;

      public ClientConfig() {
      }
   }

   @EventBusSubscriber(
      modid = "bqtweaker"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("bqtweaker")) {
            ConfigManager.sync("bqtweaker", Type.INSTANCE);
         }

      }
   }
}
