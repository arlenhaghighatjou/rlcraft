package goblinbob.mobends.core.client.event;

import goblinbob.mobends.core.client.gui.GuiBendsMenu;
import goblinbob.mobends.standard.main.MoBends;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyboardHandler {
   private static final String MAIN_CATEGORY = "Mo' Bends";
   private static final KeyBinding KEY_MENU = new KeyBinding("Mo' Bends Menu", 34, "Mo' Bends");
   private static final KeyBinding KEY_REFRESH = new KeyBinding("Refresh Animations", 68, "Mo' Bends");

   public KeyboardHandler() {
   }

   public static void initKeyBindings() {
      ClientRegistry.registerKeyBinding(KEY_MENU);
      ClientRegistry.registerKeyBinding(KEY_REFRESH);
   }

   @SubscribeEvent
   public void onKeyPressed(KeyInputEvent event) {
      if (KEY_MENU.isPressed()) {
         Minecraft.getMinecraft().displayGuiScreen(new GuiBendsMenu());
      } else if (KEY_REFRESH.isPressed()) {
         MoBends.refreshSystems();
      }

   }
}
