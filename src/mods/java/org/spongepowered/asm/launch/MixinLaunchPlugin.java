package org.spongepowered.asm.launch;

import cpw.mods.modlauncher.api.NamedPath;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService.ITransformerLoader;

public class MixinLaunchPlugin extends MixinLaunchPluginLegacy {
   public MixinLaunchPlugin() {
   }

   public void initializeLaunch(ITransformerLoader transformerLoader, NamedPath[] specialPaths) {
      this.initializeLaunch(transformerLoader);
   }
}
