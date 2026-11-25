package com.charles445.rltweaker.asm;

import com.charles445.rltweaker.asm.helper.ObfHelper;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@Name("RLTweaker ASM")
@SortingIndex(1002)
@TransformerExclusions({"com.charles445.rltweaker.asm", "com.charles445.rltweaker.asm."})
public class CoreLoader implements IFMLLoadingPlugin {
   public CoreLoader() {
   }

   public String[] getASMTransformerClass() {
      ASMConfig.setup();
      return new String[]{"com.charles445.rltweaker.asm.RLTweakerASM"};
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
