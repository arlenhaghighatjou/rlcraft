package io.github.xcube16.iseedragons.asm;

import io.github.xcube16.iseedragons.asm.helper.ObfHelper;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@MCVersion("1.12.2")
@TransformerExclusions({"io.github.xcube16.iseedragons"})
@SortingIndex(1500)
public class FMLCorePlugin implements IFMLLoadingPlugin {
   public FMLCorePlugin() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{"io.github.xcube16.iseedragons.asm.ISeeDragonsTransformer"};
   }

   public String getModContainerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
      ObfHelper.setObfuscated(((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue());
      ObfHelper.setRunsAfterDeobfRemapper(true);
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
