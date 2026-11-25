package nightkosh.advanced_fishing.core;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class Config {
   private final Configuration CONFIG;
   public static boolean overrideVanillaFishing;
   public static boolean debugMode;

   public Config(File configFile) {
      this.CONFIG = new Configuration(configFile);
   }

   public final void getConfigs() {
      this.CONFIG.load();
      overrideVanillaFishing = this.CONFIG.get("general", "OverrideVanillaFishing", true).getBoolean();
      debugMode = this.CONFIG.get("general", "DebugMode", false).getBoolean();
      this.CONFIG.save();
   }
}
