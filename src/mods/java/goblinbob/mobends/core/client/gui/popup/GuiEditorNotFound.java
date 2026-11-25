package goblinbob.mobends.core.client.gui.popup;

import net.minecraft.client.resources.I18n;

public class GuiEditorNotFound extends GuiPopUp {
   boolean errorOccurred;

   public GuiEditorNotFound(GuiPopUp.ButtonAction onBack, GuiPopUp.ButtonAction onGetEditor) {
      super(I18n.format("mobends.gui.editornotfound", new Object[0]), 200, 100, new GuiPopUp.ButtonProps[]{new GuiPopUp.ButtonProps(I18n.format("mobends.gui.back", new Object[0]), onBack), new GuiPopUp.ButtonProps(I18n.format("mobends.gui.geteditor", new Object[0]), onGetEditor)});
   }

   public void setErrorOccurred(boolean errorOccurred) {
      this.errorOccurred = errorOccurred;
   }

   public void display(int mouseX, int mouseY, float partialTicks) {
      super.display(mouseX, mouseY, partialTicks);
      if (this.errorOccurred) {
         String message = "There seems to be something wrong. Please check your internet connection, or contact the developers.";
         this.fontRenderer.drawStringWithShadow(message, 5.0F, 5.0F, -65536);
      }

   }
}
