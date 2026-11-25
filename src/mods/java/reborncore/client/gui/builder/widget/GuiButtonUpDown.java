package reborncore.client.gui.builder.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import reborncore.client.gui.builder.GuiBase;

public class GuiButtonUpDown extends GuiButton {
   GuiBase.Layer layer;
   GuiBase gui;

   public GuiButtonUpDown(int buttonId, int x, int y, GuiBase gui, GuiBase.Layer layer) {
      super(buttonId, x, y, 12, 12, "");
      this.layer = layer;
      this.gui = gui;
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (this.layer == GuiBase.Layer.FOREGROUND) {
         mouseX -= this.gui.getGuiLeft();
         mouseY -= this.gui.getGuiTop();
      }

      return this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
   }
}
