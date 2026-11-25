package com.teamacronymcoders.base.modules.debug;

import com.teamacronymcoders.base.modules.debug.items.ItemDebuggerStick;
import com.teamacronymcoders.base.modulesystem.ModuleBase;
import com.teamacronymcoders.base.registrysystem.ItemRegistry;
import com.teamacronymcoders.base.registrysystem.config.ConfigRegistry;
import com.teamacronymcoders.base.util.Platform;

public class DebugModule extends ModuleBase {
   public DebugModule() {
   }

   public String getName() {
      return "Debug";
   }

   public boolean getActiveDefault() {
      return Platform.isDevEnv();
   }

   public void registerItems(ConfigRegistry configRegistry, ItemRegistry itemRegistry) {
      itemRegistry.register(new ItemDebuggerStick());
   }
}
