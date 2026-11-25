package com.charles445.simpledifficulty.asm;

import com.charles445.simpledifficulty.asm.helper.ObfHelper;
import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@Name("SimpleDifficulty ASM")
@SortingIndex(1001)
@TransformerExclusions({"com.charles445.simpledifficulty.asm", "com.charles445.simpledifficulty.asm."})
public class CoreLoader implements IFMLLoadingPlugin {
   public CoreLoader() {
   }

   public String[] getASMTransformerClass() {
      return new String[]{"com.charles445.simpledifficulty.asm.SimpleDifficultyASM"};
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
