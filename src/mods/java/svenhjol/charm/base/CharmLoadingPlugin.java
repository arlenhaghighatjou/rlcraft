package svenhjol.charm.base;

import java.io.File;
import java.util.Map;
import net.minecraftforge.common.config.Configuration;
import svenhjol.meson.asm.MesonLoadingPlugin;

public class CharmLoadingPlugin extends MesonLoadingPlugin {
   public static Configuration config;
   public static Configuration configAsm;

   public CharmLoadingPlugin() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{"svenhjol.charm.base.CharmClassTransformer"};
   }

   public void injectData(Map<String, Object> data) {
      super.injectData(data);
      config = new Configuration(new File(((File)data.get("mcLocation")).getPath() + "/config/charm.cfg"), true);
      config.load();
      configAsm = new Configuration(new File(((File)data.get("mcLocation")).getPath() + "/config/charm_asm.cfg"), true);
      configAsm.load();
   }
}
