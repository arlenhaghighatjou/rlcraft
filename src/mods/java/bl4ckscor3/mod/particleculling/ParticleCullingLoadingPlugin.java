package bl4ckscor3.mod.particleculling;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

@MCVersion("1.12.2")
public class ParticleCullingLoadingPlugin implements IFMLLoadingPlugin {
   public ParticleCullingLoadingPlugin() {
      MixinBootstrap.init();
      Mixins.addConfiguration("mixins.particleculling.json");
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
