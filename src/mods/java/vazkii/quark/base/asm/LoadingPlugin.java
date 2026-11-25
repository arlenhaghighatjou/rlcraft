package vazkii.quark.base.asm;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@MCVersion("1.12.2")
@Name("Quark Plugin")
@TransformerExclusions({"vazkii.quark.base.asm"})
@SortingIndex(1001)
public class LoadingPlugin implements IFMLLoadingPlugin {
   public static boolean runtimeDeobfEnabled = false;

   public LoadingPlugin() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{"vazkii.quark.base.asm.ClassTransformer"};
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
