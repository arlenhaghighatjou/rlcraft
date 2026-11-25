package reborncore.client.gui.builder.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import reborncore.client.gui.builder.GuiBase;
import reborncore.common.powerSystem.PowerSystem;

public class GuiButtonPowerBar extends GuiButton {
   GuiBase.Layer layer;
   GuiBase gui;

   public GuiButtonPowerBar(int buttonId, int x, int y, GuiBase gui, GuiBase.Layer layer) {
      super(buttonId, x, y, 12, 48, "");
      this.layer = layer;
      this.gui = gui;
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (this.layer == GuiBase.Layer.FOREGROUND) {
         mouseX -= this.gui.getGuiLeft();
         mouseY -= this.gui.getGuiTop();
      }

      if (this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height) {
         PowerSystem.bumpPowerConfig();
         return true;
      } else {
         return false;
      }
   }

   public void drawButtonForegroundLayer(int mouseX, int mouseY) {
   }

   public void drawButton(Minecraft p_191745_1_, int p_191745_2_, int p_191745_3_, float p_191745_4_) {
   }
}
