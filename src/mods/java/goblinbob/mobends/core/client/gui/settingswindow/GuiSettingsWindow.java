package goblinbob.mobends.core.client.gui.settingswindow;

import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.bender.EntityBender;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.client.gui.GuiBendsMenu;
import goblinbob.mobends.core.client.gui.elements.GuiCompactTextField;
import goblinbob.mobends.core.util.Draw;
import goblinbob.mobends.core.util.GuiHelper;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class GuiSettingsWindow extends GuiScreen {
   public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("mobends", "textures/gui/pack_window.png");
   public static final int EDITOR_WIDTH = 280;
   public static final int EDITOR_HEIGHT = 177;
   private static final int COMPONENT_BUTTON_BACK = 0;
   private static final int COMPONENT_QUERY_INPUT = 1;
   private int x;
   private int y;
   private GuiCompactTextField filterQueryInput;
   private final GuiBenderList bendsSettingsListUI = new GuiBenderList(0, 0, 270, 147);
   private final EntityBenderRegistry.Filter filter = new EntityBenderRegistry.Filter();

   public GuiSettingsWindow() {
      this.fetchBenders();
   }

   public void initGui() {
      super.initGui();
      Keyboard.enableRepeatEvents(true);
      this.x = (this.width - 280) / 2;
      this.y = (this.height - 177) / 2;
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, 10, this.height - 30, 60, 20, I18n.format("mobends.gui.back", new Object[0])));
      this.filterQueryInput = new GuiCompactTextField(1, this.fontRenderer, this.x + 6, this.y + 6, 150, 16);
      this.filterQueryInput.setFocused(true);
      this.filterQueryInput.setPlaceholderText(I18n.format("mobends.gui.search", new Object[0]));
      this.bendsSettingsListUI.initGui(this.x + 9, this.y + 9 + 20);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
      Draw.borderBox(this.x + 4, this.y + 4, 280, 177, 4, 36, 126);
      Draw.texturedModalRect(this.x, this.y - 13, 101, 0, 4, 16);
      Draw.texturedModalRect(this.x + 4, this.y - 13, 264, 16, 105, 0, 1, 16);
      Draw.texturedModalRect(this.x + 280 - 17, this.y - 13, 106, 0, 19, 16);
      this.bendsSettingsListUI.draw(DataUpdateHandler.partialTicks);
      this.fontRenderer.drawStringWithShadow(I18n.format("mobends.gui.settings", new Object[0]), (float)(this.x + 6), (float)(this.y - 9), 16777215);
      this.filterQueryInput.drawTextBox();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public void updateScreen() {
      super.updateScreen();
      int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      this.bendsSettingsListUI.update(mouseX, mouseY);
      this.filterQueryInput.updateCursorCounter();
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.bendsSettingsListUI.handleMouseClicked(mouseX, mouseY, mouseButton);
      this.filterQueryInput.mouseClicked(mouseX, mouseY, mouseButton);
   }

   protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
      super.mouseReleased(mouseX, mouseY, mouseButton);
      this.bendsSettingsListUI.handleMouseReleased(mouseX, mouseY, mouseButton);
   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.bendsSettingsListUI.handleMouseInput();
   }

   public void keyTyped(char typedChar, int keyCode) {
      this.filterQueryInput.textboxKeyTyped(typedChar, keyCode);
      if (!this.filterQueryInput.getText().equals(this.filter.query)) {
         this.filter.query = this.filterQueryInput.getText();
         this.fetchBenders();
      }

      switch(keyCode) {
      case 1:
         Core.saveConfiguration();
         GuiHelper.closeGui();
      default:
      }
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
      switch(button.id) {
      case 0:
         this.goBack();
      default:
      }
   }

   private void goBack() {
      Core.saveConfiguration();
      this.mc.displayGuiScreen(new GuiBendsMenu());
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void fetchBenders() {
      this.bendsSettingsListUI.clearElements();

      for(EntityBender<?> bender : EntityBenderRegistry.instance.getRegistered(this.filter)) {
         this.bendsSettingsListUI.addElement(new GuiBenderSettings(bender));
      }

   }
}
