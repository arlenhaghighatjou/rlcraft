package org.orecruncher.dsurround.mixins;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

@MCVersion("1.12.2")
public class TransformLoader implements IFMLLoadingPlugin {
   public TransformLoader() {
   }

   public String getModContainerClass() {
      return null;
   }

   public String[] getASMTransformerClass() {
      return new String[0];
   }

   public String getAccessTransformerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> map) {
   }

   static {
      MixinBootstrap.init();
      Mixins.addConfiguration("mixins.dsurround.json");
   }
}
