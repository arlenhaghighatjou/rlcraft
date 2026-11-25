package snownee.snow.core;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import org.spongepowered.asm.launch.MixinBootstrap;

@Name("Snow! Real Magic!")
public class CoreMod implements IFMLLoadingPlugin {
   public CoreMod() {
      MixinBootstrap.init();
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
