package goblinbob.mobends.core.client.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiCustomButton extends GuiButton {
   private final Minecraft mc = Minecraft.getMinecraft();

   public GuiCustomButton(int buttonId, int width, int height) {
      super(buttonId, 0, 0, width, height, "");
   }

   public GuiCustomButton(int width, int height, String text) {
      super(0, 0, 0, width, height, text);
   }

   public GuiCustomButton setPosition(int x, int y) {
      this.x = x;
      this.y = y;
      return this;
   }

   public void drawButton(int mouseX, int mouseY, float partialTicks) {
      super.drawButton(this.mc, mouseX, mouseY, partialTicks);
   }

   public boolean mousePressed(int mouseX, int mouseY) {
      boolean clicked = this.mousePressed(this.mc, mouseX, mouseY);
      if (clicked) {
         this.playPressSound(this.mc.getSoundHandler());
      }

      return clicked;
   }

   public GuiCustomButton setText(String text) {
      this.displayString = text;
      return this;
   }
}
