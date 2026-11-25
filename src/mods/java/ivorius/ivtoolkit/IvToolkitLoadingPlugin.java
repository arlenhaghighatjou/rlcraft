package ivorius.ivtoolkit;

import ivorius.ivtoolkit.tools.JavaCompatibility;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@Name("IvToolkit")
@TransformerExclusions({"ivorius.ivtoolkit.asm."})
public class IvToolkitLoadingPlugin implements IFMLLoadingPlugin {
   public IvToolkitLoadingPlugin() {
   }

   public String[] getASMTransformerClass() {
      JavaCompatibility.requireJava8(true);
      return new String[0];
   }

   public String getModContainerClass() {
      return "ivorius.ivtoolkit.IvToolkitCoreContainer";
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
