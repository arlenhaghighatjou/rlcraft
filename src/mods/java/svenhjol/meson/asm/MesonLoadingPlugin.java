package svenhjol.meson.asm;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public abstract class MesonLoadingPlugin implements IFMLLoadingPlugin {
   public static boolean obf = false;

   public MesonLoadingPlugin() {
   }

   public String getModContainerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
      obf = ((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue();
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
