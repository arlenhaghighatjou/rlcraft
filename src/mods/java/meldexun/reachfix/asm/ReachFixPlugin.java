package meldexun.reachfix.asm;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@MCVersion("1.12.2")
@TransformerExclusions({"meldexun.reachfix.asm"})
public class ReachFixPlugin implements IFMLLoadingPlugin {
   public ReachFixPlugin() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{"meldexun.reachfix.asm.ReachFixClassTransformer"};
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
