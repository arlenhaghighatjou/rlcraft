package hunternif.mc.atlas.client;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.client.gui.GuiAtlas;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyHandler {
   private static final int KEY_ATLAS = 0;
   private static List<KeyBinding> bindings = new ArrayList(1);

   public KeyHandler() {
   }

   public static void registerBindings() {
      bindings.add(0, new KeyBinding("key.openatlas.desc", 50, "key.antiqueatlas.category"));
      bindings.forEach(ClientRegistry::registerKeyBinding);
   }

   public void register(EventBus bus) {
      bus.register(KeyInputEvent.class, EventPriority.NORMAL, true, this::onKeyEvent);
   }

   public void onKeyEvent(KeyInputEvent event) {
      if (((KeyBinding)bindings.get(0)).isPressed()) {
         GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
         if (currentScreen instanceof GuiAtlas) {
            ((GuiAtlas)currentScreen).close();
         } else {
            AntiqueAtlasMod.proxy.openAtlasGUI();
         }
      }

   }
}
