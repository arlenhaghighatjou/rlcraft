package net.silentchaos512.lib.client.gui.button;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiDropDownElement extends GuiButton {
   GuiDropDownList parent;
   private final Consumer<GuiDropDownElement> action;

   public GuiDropDownElement(int buttonId, String buttonText, Consumer<GuiDropDownElement> action) {
      super(buttonId, 0, 0, 100, 12, buttonText);
      this.action = action;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      super.drawButton(mc, mouseX, mouseY, partialTicks);
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      return this.visible && super.mousePressed(mc, mouseX, mouseY);
   }

   public void mouseReleased(int mouseX, int mouseY) {
      if (this.parent != null) {
         this.parent.onElementSelected(this);
         this.action.accept(this);
      }

   }
}
