package reborncore.client.guibuilder;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public class GuiBulderTest extends GuiContainer {
   GuiBuilder builder = new GuiBuilder(GuiBuilder.defaultTextureSheet);

   public GuiBulderTest(Container inventorySlotsIn) {
      super(inventorySlotsIn);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      this.builder.drawDefaultBackground(this, this.guiLeft, this.guiLeft, this.xSize, this.ySize);
      this.builder.drawSlot(this, this.guiLeft + 40, this.guiTop + 30);
      this.builder.drawSlot(this, this.guiLeft + 120, this.guiTop + 30);
      this.builder.drawPlayerSlots(this, this.guiLeft + this.xSize / 2, this.guiTop + 80, true);
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
   }
}
