package fionathemortal.betterbiomeblend.core;

import fionathemortal.betterbiomeblend.BetterBiomeBlend;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;

@MCVersion("1.12.2")
@Name("Better Biome Blend")
public class CoreMod implements IFMLLoadingPlugin {
   public static boolean foundMixinFramework;

   public CoreMod() {
   }

   public String[] getASMTransformerClass() {
      return new String[0];
   }

   public String getModContainerClass() {
      return null;
   }

   @Nullable
   public String getSetupClass() {
      Class<?> mixinClass = null;

      try {
         mixinClass = Class.forName("org.spongepowered.asm.launch.MixinTweaker");
      } catch (ClassNotFoundException var3) {
         var3.printStackTrace();
      }

      String result = null;
      if (mixinClass != null) {
         foundMixinFramework = true;
         result = CoreModMixinInterface.class.getName();
         BetterBiomeBlend.LOGGER.info("Found valid Mixin framework! Proceeding to load.");
      } else {
         BetterBiomeBlend.LOGGER.info("No valid Mixin framework found! Loading not possible.");
      }

      return result;
   }

   public void injectData(Map<String, Object> data) {
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
