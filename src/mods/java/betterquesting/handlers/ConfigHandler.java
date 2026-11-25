package betterquesting.handlers;

import betterquesting.api.storage.BQ_Settings;
import betterquesting.core.BetterQuesting;
import net.minecraftforge.common.config.Configuration;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class ConfigHandler {
   public static Configuration config;

   public ConfigHandler() {
   }

   public static void initConfigs() {
      if (config == null) {
         BetterQuesting.logger.log(Level.ERROR, "Config attempted to be loaded before it was initialised!");
      } else {
         config.load();
         BQ_Settings.questNotices = config.getBoolean("Quest Notices", "general", true, "Enabled the popup notices when quests are completed or updated");
         BQ_Settings.curTheme = config.getString("Theme", "general", "betterquesting:light", "The current questing theme");
         BQ_Settings.useBookmark = config.getBoolean("Use Quest Bookmark", "general", true, "Jumps the user to the last opened quest");
         BQ_Settings.guiWidth = config.getInt("Max GUI Width", "general", -1, -1, Integer.MAX_VALUE, "Clamps the max UI width (-1 to disable)");
         BQ_Settings.guiHeight = config.getInt("Max GUI Height", "general", -1, -1, Integer.MAX_VALUE, "Clamps the max UI height (-1 to disable)");
         config.save();
      }
   }
}
