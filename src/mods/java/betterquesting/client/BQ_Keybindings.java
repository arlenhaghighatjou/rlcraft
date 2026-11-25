package betterquesting.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class BQ_Keybindings {
   public static KeyBinding openQuests;

   public BQ_Keybindings() {
   }

   public static void RegisterKeys() {
      openQuests = new KeyBinding("key.betterquesting.quests", 41, "BetterQuesting");
      ClientRegistry.registerKeyBinding(openQuests);
   }
}
