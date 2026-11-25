package ljfa.tntutils.asm;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@Name("TNTUtilities Core")
@SortingIndex(1100)
@TransformerExclusions({"ljfa.tntutils"})
public class TntuPlugin implements IFMLLoadingPlugin {
   public TntuPlugin() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{ExplosionTransformer.class.getName()};
   }

   public String getModContainerClass() {
      return TntuCoremodContainer.class.getName();
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
