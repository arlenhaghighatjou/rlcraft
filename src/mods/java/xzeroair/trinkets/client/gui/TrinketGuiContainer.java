package xzeroair.trinkets.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public class TrinketGuiContainer extends GuiContainer {
   public TrinketGuiContainer(Container inventorySlotsIn) {
      super(inventorySlotsIn);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      super.drawGuiContainerForegroundLayer(mouseX, mouseY);
   }
}
