package de.mennomax.astikorcarts.init;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModKeybindings {
   public static List<KeyBinding> keybindings = new ArrayList();

   public ModKeybindings() {
   }

   public static void registerKeyBindings() {
      keybindings.add(new KeyBinding("key.astikorcarts.desc", 19, "key.categories.astikorcarts"));

      for(KeyBinding bind : keybindings) {
         ClientRegistry.registerKeyBinding(bind);
      }

   }
}
