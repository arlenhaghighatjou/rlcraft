package goblinbob.mobends.core.client.gui.popup;

import goblinbob.mobends.core.client.gui.elements.GuiCompactTextField;
import net.minecraft.client.resources.I18n;

public class GuiPopUpCreatePack extends GuiPopUp {
   protected GuiCompactTextField titleTextField;

   public GuiPopUpCreatePack() {
      super(I18n.format("mobends.gui.createpack", new Object[0]), new GuiPopUp.ButtonProps[]{new GuiPopUp.ButtonProps("Cancel", () -> {
      }), new GuiPopUp.ButtonProps("Create", () -> {
      })});
   }

   public void initGui(int x, int y) {
      super.initGui(x, y);
      this.titleTextField.setPosition(this.x + 5, this.y + 39);
   }

   public void display(int mouseX, int mouseY, float partialTicks) {
      super.display(mouseX, mouseY, partialTicks);
      this.titleTextField.drawTextBox();
   }

   public void update(int mouseX, int mouseY) {
      super.update(mouseX, mouseY);
      if (this.titleTextField.isFocused()) {
         this.titleTextField.updateCursorCounter();
      }

   }

   public void mouseClicked(int mouseX, int mouseY, int button) {
      this.titleTextField.mouseClicked(mouseX, mouseY, button);
      super.mouseClicked(mouseX, mouseY, button);
   }

   public void keyTyped(char typedChar, int keyCode) {
      this.titleTextField.textboxKeyTyped(typedChar, keyCode);
   }
}
