package meldexun.renderlib.asm;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

@MCVersion("1.12.2")
@TransformerExclusions({"meldexun.renderlib.asm"})
public class RenderLibPlugin implements IFMLLoadingPlugin {
   public RenderLibPlugin() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{"meldexun.renderlib.asm.RenderLibClassTransformer"};
   }

   public String getModContainerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
      if (Boolean.FALSE.equals(data.get("runtimeDeobfuscationEnabled"))) {
         MixinBootstrap.init();
         MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
      }

   }

   public String getAccessTransformerClass() {
      return null;
   }
}
