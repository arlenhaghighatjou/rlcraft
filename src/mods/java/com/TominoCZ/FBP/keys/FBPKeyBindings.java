package com.TominoCZ.FBP.keys;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class FBPKeyBindings {
   public static KeyBinding FBPMenu;
   public static KeyBinding FBPFreeze;
   public static KeyBinding FBPToggle;
   public static KeyBinding FBPSweep;
   public static KeyBinding FBPFastAdd;

   public FBPKeyBindings() {
   }

   public static void init() {
      FBPMenu = new KeyBinding("Open Menu", 25, "Fancy Block Particles");
      FBPFreeze = new KeyBinding("Toggle Freeze Effect", 19, "Fancy Block Particles");
      FBPToggle = new KeyBinding("Enable/Disable", 0, "Fancy Block Particles");
      FBPSweep = new KeyBinding("Kill Particles", 0, "Fancy Block Particles");
      FBPFastAdd = new KeyBinding("Blacklist Block", 45, "Fancy Block Particles");
      ClientRegistry.registerKeyBinding(FBPMenu);
      ClientRegistry.registerKeyBinding(FBPFreeze);
      ClientRegistry.registerKeyBinding(FBPToggle);
      ClientRegistry.registerKeyBinding(FBPSweep);
      ClientRegistry.registerKeyBinding(FBPFastAdd);
   }
}
