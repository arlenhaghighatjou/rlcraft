package tschipp.callablehorses.client.keybinds;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tschipp.callablehorses.common.config.CallableHorsesConfig;

public class KeybindManager {
   public static KeyBinding setHorse;
   public static KeyBinding callHorse;
   public static KeyBinding showStats;

   public KeybindManager() {
   }

   @SideOnly(Side.CLIENT)
   public static void init() {
      setHorse = new KeyBinding("key.sethorse.desc", 25, "key.callablehorses.category");
      callHorse = new KeyBinding("key.callhorse.desc", 47, "key.callablehorses.category");
      showStats = new KeyBinding("key.showstats.desc", 37, "key.callablehorses.category");
      ClientRegistry.registerKeyBinding(setHorse);
      ClientRegistry.registerKeyBinding(callHorse);
      if (CallableHorsesConfig.settings.enableStatsViewer) {
         ClientRegistry.registerKeyBinding(showStats);
      }

   }
}
