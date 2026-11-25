package net.theawesomegem.fishingmadebetter.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeybindManager {
   public static KeyBinding reelIn;
   public static KeyBinding reelOut;

   public KeybindManager() {
   }

   public static void load() {
      reelIn = new KeyBinding("key.reelIn", 203, "key.fishingmadebetter.category");
      reelOut = new KeyBinding("key.reelOut", 205, "key.fishingmadebetter.category");
      ClientRegistry.registerKeyBinding(reelIn);
      ClientRegistry.registerKeyBinding(reelOut);
   }
}
