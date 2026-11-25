package codersafterdark.reskillable.client.gui.handler;

import codersafterdark.reskillable.Reskillable;
import codersafterdark.reskillable.client.gui.GuiSkills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindings {
   public static KeyBinding openGUI = new KeyBinding(Reskillable.proxy.getLocalizedString("key.open_gui"), 21, Reskillable.proxy.getLocalizedString("key.controls.reskillable"));

   public KeyBindings() {
   }

   public static void init() {
      ClientRegistry.registerKeyBinding(openGUI);
   }

   @SubscribeEvent
   public void onKeyInput(KeyInputEvent event) {
      this.keyTyped(openGUI);
   }

   private void keyTyped(KeyBinding binding) {
      Minecraft minecraft = FMLClientHandler.instance().getClient();
      if (binding.isPressed() && minecraft.currentScreen == null) {
         Minecraft.getMinecraft().displayGuiScreen(new GuiSkills());
      }

   }
}
