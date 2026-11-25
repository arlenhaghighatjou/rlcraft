package me.jellysquid.mods.phosphor.core;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion("1.12.2")
public class PhosphorFMLLoadingPlugin implements IFMLLoadingPlugin {
   public PhosphorFMLLoadingPlugin() {
   }

   public String[] getASMTransformerClass() {
      return new String[0];
   }

   public String getModContainerClass() {
      return null;
   }

   @Nullable
   public String getSetupClass() {
      return PhosphorFMLSetupHook.class.getName();
   }

   public void injectData(Map<String, Object> data) {
   }

   public String getAccessTransformerClass() {
      return null;
   }
}
