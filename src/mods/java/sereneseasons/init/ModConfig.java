package sereneseasons.init;

import com.google.common.collect.Lists;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.List;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.ConfigHandler;
import sereneseasons.config.SeasonsConfig;

public class ModConfig {
   public static List<ConfigHandler> configHandlers = Lists.newArrayList();
   public static SeasonsConfig seasons;

   public ModConfig() {
   }

   public static void preInit(VFile2 configDir) {
      seasons = new SeasonsConfig(new VFile2(configDir, "seasons.cfg"));
   }

   public static void init(VFile2 configDir) {
      BiomeConfig.init(configDir);
   }
}
