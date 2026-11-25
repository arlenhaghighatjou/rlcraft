package pw.prok.realbench.asm;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion("1.12.2")
public class RBLoadingPlugin implements IFMLLoadingPlugin {
   public RBLoadingPlugin() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{"pw.prok.realbench.asm.RBTransformer"};
   }

   public String getModContainerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
   }

   public String getAccessTransformerClass() {
      return "pw.prok.realbench.asm.RBAccessTransformer";
   }
}
