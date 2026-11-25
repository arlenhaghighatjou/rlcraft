package pl.asie.foamfix.coremod;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import pl.asie.foamfix.shared.FoamFixShared;

@Name("Do not report to Forge! (If you haven't disabled the FoamFix coremod, try disabling it in the config! Note that this bit of text will still appear.)")
@SortingIndex(1001)
@TransformerExclusions({"pl.asie.foamfix"})
public class FoamFixCore implements IFMLLoadingPlugin {
   public FoamFixCore() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{"pl.asie.foamfix.coremod.FoamFixTransformer"};
   }

   public String getModContainerClass() {
      return "pl.asie.foamfix.coremod.FoamFixCoreContainer";
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
      FoamFixShared.config.init(new File(new File("config"), "foamfix.cfg"), true);
      if (FoamFixShared.isCoremod) {
         if (FoamFixShared.config.clInitOptions) {
            try {
               File optionsFile = new File("options.txt");
               if (!optionsFile.exists()) {
                  Files.write("mipmapLevels:0\n", optionsFile, Charsets.UTF_8);
               }

               File forgeCfgFile = new File(new File("config"), "forge.cfg");
               if (!forgeCfgFile.exists()) {
                  Files.write("client {\nB:alwaysSetupTerrainOffThread=true\n}\n", forgeCfgFile, Charsets.UTF_8);
               }
            } catch (IOException var4) {
               var4.printStackTrace();
            }
         }

         if (this.getClass().getClassLoader() instanceof LaunchClassLoader) {
            if (FoamFixShared.config.geBlacklistLibraryTransformers) {
               LaunchClassLoader classLoader = (LaunchClassLoader)this.getClass().getClassLoader();
               classLoader.addTransformerExclusion("com.ibm.icu.");
               classLoader.addTransformerExclusion("com.sun.");
               classLoader.addTransformerExclusion("gnu.trove.");
               classLoader.addTransformerExclusion("io.netty.");
               classLoader.addTransformerExclusion("it.unimi.dsi.fastutil.");
               classLoader.addTransformerExclusion("joptsimple.");
               classLoader.addTransformerExclusion("org.apache.");
               classLoader.addTransformerExclusion("oshi.");
               classLoader.addTransformerExclusion("scala.");
            }

            LaunchClassLoader var6 = (LaunchClassLoader)this.getClass().getClassLoader();
         }

         FoamFixTransformer.init();
      }

   }

   public String getAccessTransformerClass() {
      return "pl.asie.foamfix.coremod.FoamFixAT";
   }
}
