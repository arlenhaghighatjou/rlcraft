package codersafterdark.reskillable.base.asm;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class LoadingPlugin implements IFMLLoadingPlugin {
   public static boolean runtimeDeobfEnabled;

   public LoadingPlugin() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{"codersafterdark.reskillable.base.asm.ClassTransformer"};
   }

   public String getModContainerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
      runtimeDeobfEnabled = ((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue();
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
