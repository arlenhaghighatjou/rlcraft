package dev.satyrn.wolfarmor.core;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

@MCVersion("1.12.2")
@TransformerExclusions({"dev.satyrn.wolfarmor.core"})
public class WolfArmorCore implements IFMLLoadingPlugin {
   public WolfArmorCore() {
      MixinBootstrap.init();
      Mixins.addConfiguration("mixins.wolfarmor.core.json");
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
