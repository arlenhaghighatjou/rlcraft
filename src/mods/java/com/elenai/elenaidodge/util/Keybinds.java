package com.elenai.elenaidodge.util;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class Keybinds {
   public static KeyBinding dodge;

   public Keybinds() {
   }

   public static void register() {
      dodge = new KeyBinding("key.elenaidodge.dodge", 56, "key.categories.movement");
      ClientRegistry.registerKeyBinding(dodge);
   }
}
