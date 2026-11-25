package sereneseasons.asm;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class SSLoadingPlugin implements IFMLLoadingPlugin {
   public SSLoadingPlugin() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{"sereneseasons.asm.transformer.EntityRendererTransformer", "sereneseasons.asm.transformer.WorldTransformer"};
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
      return null;
   }
}
