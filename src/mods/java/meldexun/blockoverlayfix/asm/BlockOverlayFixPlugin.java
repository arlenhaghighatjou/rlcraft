package meldexun.blockoverlayfix.asm;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

@MCVersion("1.12.2")
@TransformerExclusions({"meldexun.blockoverlayfix.asm", "meldexun.blockoverlayfix.mixin"})
public class BlockOverlayFixPlugin implements IFMLLoadingPlugin {
   public BlockOverlayFixPlugin() {
   }

   public String[] getASMTransformerClass() {
      return new String[0];
   }

   public String getModContainerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
      MixinBootstrap.init();
      Mixins.addConfiguration("mixins.blockoverlayfix.json");
      MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
