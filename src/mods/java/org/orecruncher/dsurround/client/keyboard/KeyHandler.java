package org.orecruncher.dsurround.client.keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.lib.compat.ModEnvironment;

@SideOnly(Side.CLIENT)
public class KeyHandler {
   private static final String SECTION_NAME = "Dynamic Surroundings";
   public static KeyBinding ANIMANIA_BADGES;

   public KeyHandler() {
   }

   public static void init() {
      if (ModEnvironment.Animania.isLoaded()) {
         ANIMANIA_BADGES = new KeyBinding("dsurround.cfg.keybind.AnimaniaBadges", 0, "Dynamic Surroundings");
         ClientRegistry.registerKeyBinding(ANIMANIA_BADGES);
      }

   }
}
