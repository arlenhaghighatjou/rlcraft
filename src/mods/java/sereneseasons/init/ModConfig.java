package sereneseasons.init;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.ConfigHandler;
import sereneseasons.config.SeasonsConfig;

public class ModConfig {
   public static List<ConfigHandler> configHandlers = Lists.newArrayList();
   public static SeasonsConfig seasons;

   public ModConfig() {
   }

   public static void preInit(File configDir) {
      seasons = new SeasonsConfig(new File(configDir, "seasons.cfg"));
   }

   public static void init(File configDir) {
      BiomeConfig.init(configDir);
   }
}
