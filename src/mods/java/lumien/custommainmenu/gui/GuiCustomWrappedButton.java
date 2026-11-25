package lumien.custommainmenu.gui;

import lumien.custommainmenu.configuration.elements.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiCustomWrappedButton extends GuiCustomButton {
   GuiButton wrappedButton;
   public int wrappedButtonID;

   public GuiCustomWrappedButton(int buttonId, int wrappedButtonID, Button b) {
      super(buttonId, b);
      this.wrappedButtonID = wrappedButtonID;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.wrappedButton != null) {
         this.visible = this.enabled = this.wrappedButton.visible && this.wrappedButton.enabled;
      } else {
         this.visible = this.enabled = false;
      }

      super.drawButton(mc, mouseX, mouseY, partialTicks);
   }

   public void init(GuiButton wrappedButton) {
      this.wrappedButton = wrappedButton;
      if (wrappedButton == null) {
         this.visible = this.enabled = false;
      }

   }

   public GuiButton getWrappedButton() {
      return this.wrappedButton;
   }
}
