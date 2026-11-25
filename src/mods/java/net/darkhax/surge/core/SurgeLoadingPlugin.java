package net.darkhax.surge.core;

import java.io.File;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

@MCVersion("1.12.2")
@SortingIndex(1001)
@TransformerExclusions({"net.darkhax.surge.core"})
public class SurgeLoadingPlugin implements IFMLLoadingPlugin {
   public static final Logger LOG = LogManager.getLogger("Surge");
   public static long firstLaunchTime = 0L;

   public SurgeLoadingPlugin() {
      SurgeConfiguration.init(new File("config/surge.cfg"));
      firstLaunchTime = System.currentTimeMillis();
      MixinBootstrap.init();
      Mixins.addConfiguration("mixins.surge.json");
   }

   public String[] getASMTransformerClass() {
      return new String[0];
   }

   public String getModContainerClass() {
      return null;
   }

   @Nullable
   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
