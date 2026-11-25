package com.oblivioussp.spartanweaponry.mixin;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

@MCVersion("1.12.2")
@Name("SpartanWeaponry-MixinLoader")
@SortingIndex(-4000)
public class MixinLoader implements IFMLLoadingPlugin {
   public MixinLoader() {
      MixinBootstrap.init();
      Mixins.addConfiguration("spartanweaponry.mixins.json");
   }

   public String[] getASMTransformerClass() {
      return new String[0];
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
