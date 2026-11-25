package bqtweaker.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class BQTweaker_Keybindings {
   public static KeyBinding pageBack;

   public BQTweaker_Keybindings() {
   }

   public static void RegisterKeys() {
      pageBack = new KeyBinding("bqtweaker.key.pageback", 14, "BQTweaker");
      ClientRegistry.registerKeyBinding(pageBack);
   }
}
