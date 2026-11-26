package nightkosh.advanced_fishing.core;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraftforge.common.config.Configuration;

public class Config {
   private final Configuration CONFIG;
   public static boolean overrideVanillaFishing;
   public static boolean debugMode;

   public Config(VFile2 configFile) {
      this.CONFIG = new Configuration(configFile);
   }

   public final void getConfigs() {
      this.CONFIG.load();
      overrideVanillaFishing = this.CONFIG.get("general", "OverrideVanillaFishing", true).getBoolean();
      debugMode = this.CONFIG.get("general", "DebugMode", false).getBoolean();
      this.CONFIG.save();
   }
}
