package com.endertech.minecraft.forge.coremod;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@SortingIndex(0)
@TransformerExclusions({"com.endertech.minecraft.forge.configs"})
public final class CorePlugin implements IFMLLoadingPlugin {
   private static boolean obfuscatedCode = false;

   public CorePlugin() {
   }

   public String getModContainerClass() {
      return null;
   }

   public String getSetupClass() {
      return null;
   }

   public void injectData(Map<String, Object> data) {
      obfuscatedCode = ((Boolean)data.get("runtimeDeobfuscationEnabled")).booleanValue();
   }

   public String getAccessTransformerClass() {
      return null;
   }

   public static boolean obfuscatedCode() {
      return obfuscatedCode;
   }

   public String[] getASMTransformerClass() {
      return null;
   }
}
