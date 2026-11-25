package us.getfluxed.controlsearch.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import us.getfluxed.controlsearch.client.gui.GuiNewControls;

public class ClientEventHandler {
   public ClientEventHandler() {
   }

   @SubscribeEvent
   public void guiInit(GuiOpenEvent e) {
      if (e.getGui() instanceof GuiControls && !(e.getGui() instanceof GuiNewControls)) {
         e.setGui(new GuiNewControls(Minecraft.getMinecraft().currentScreen, Minecraft.getMinecraft().gameSettings));
      }

   }
}
