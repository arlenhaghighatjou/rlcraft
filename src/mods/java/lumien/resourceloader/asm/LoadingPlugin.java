package lumien.resourceloader.asm;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

@SortingIndex(1001)
public class LoadingPlugin implements IFMLLoadingPlugin {
   public static boolean IN_MCP = false;

   public LoadingPlugin() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{ClassTransformer.class.getName()};
   }

   public String getModContainerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
      IN_MCP = !((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue();
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
