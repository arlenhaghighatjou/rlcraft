package com.github.alexthe666.iceandfire.core;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModKeys {
   public static KeyBinding dragon_fireAttack;
   public static KeyBinding dragon_strike;
   public static KeyBinding dragon_down;
   public static KeyBinding dragon_change_view;

   public ModKeys() {
   }

   public static void init() {
      dragon_fireAttack = new KeyBinding("key.dragon_fireAttack", 19, "key.categories.gameplay");
      dragon_strike = new KeyBinding("key.dragon_strike", 34, "key.categories.gameplay");
      dragon_down = new KeyBinding("key.dragon_down", 45, "key.categories.gameplay");
      dragon_change_view = new KeyBinding("key.dragon_change_view", 65, "key.categories.misc");
      ClientRegistry.registerKeyBinding(dragon_fireAttack);
      ClientRegistry.registerKeyBinding(dragon_strike);
      ClientRegistry.registerKeyBinding(dragon_down);
      ClientRegistry.registerKeyBinding(dragon_change_view);
   }
}
