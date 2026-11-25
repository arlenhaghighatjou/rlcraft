package com.Shultrea.Rin.Transformer;

import com.Shultrea.Rin.Transformer.helper.ObfHelper;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@Name("SoManyEnchantments ASM")
@SortingIndex(1002)
@TransformerExclusions({"com.Shultrea.Rin.Transformer", "com.Shultrea.Rin.Transformer."})
public class CoreLoader implements IFMLLoadingPlugin {
   public CoreLoader() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{"com.Shultrea.Rin.Transformer.SMEASM"};
   }

   public void injectData(Map<String, Object> data) {
      ObfHelper.setObfuscated(((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue());
      ObfHelper.setRunsAfterDeobfRemapper(true);
   }

   public String getModContainerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
