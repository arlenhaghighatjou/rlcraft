package dk.zlepper.itlt;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindings {
   private static KeyBinding Enter;
   private static KeyBinding Return;

   public KeyBindings() {
   }

   public static void init() {
      Enter = new KeyBinding("Enter", 28, "key.categories.itlt");
      Return = new KeyBinding("Other Enter", 156, "key.categories.itlt");
      ClientRegistry.registerKeyBinding(Enter);
      ClientRegistry.registerKeyBinding(Return);
   }

   public static boolean isEnterPressed() {
      return Enter.isPressed() || Return.isPressed();
   }
}
