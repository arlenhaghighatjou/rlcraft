package goblinbob.mobends.core.client.gui;

import goblinbob.mobends.core.WebAPI;
import goblinbob.mobends.core.client.gui.elements.GuiSectionButton;
import goblinbob.mobends.core.client.gui.packswindow.GuiPacksWindow;
import goblinbob.mobends.core.client.gui.popup.GuiEditorNotFound;
import goblinbob.mobends.core.client.gui.popup.GuiPopUp;
import goblinbob.mobends.core.client.gui.settingswindow.GuiSettingsWindow;
import goblinbob.mobends.core.network.NetworkConfiguration;
import goblinbob.mobends.core.util.Draw;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiBendsMenu extends GuiScreen {
   private static final ResourceLocation MENU_TITLE_TEXTURE = new ResourceLocation("mobends", "textures/gui/title.png");
   public static final ResourceLocation ICONS_TEXTURE = new ResourceLocation("mobends", "textures/gui/icons.png");
   private GuiSectionButton settingsButton;
   private GuiSectionButton packsButton;
   private GuiSectionButton customizeButton;
   private GuiPopUp popUp;

   public GuiBendsMenu() {
      Keyboard.enableRepeatEvents(true);
      this.settingsButton = (new GuiSectionButton(I18n.format("mobends.gui.section.settings", new Object[0]), -2475520)).setLeftIcon(0, 43, 19, 19).setRightIcon(19, 43, 19, 19);
      this.packsButton = (new GuiSectionButton(I18n.format("mobends.gui.section.packs", new Object[0]), -12224546)).setLeftIcon(38, 43, 23, 20).setRightIcon(38, 43, 23, 20);
      this.customizeButton = (new GuiSectionButton(I18n.format("mobends.gui.section.customize", new Object[0]), -14230877)).setLeftIcon(80, 43, 19, 14).setRightIcon(80, 43, 19, 14);
      this.popUp = null;
   }

   public void initGui() {
      super.initGui();
      this.buttonList.clear();
      if (this.popUp != null) {
         this.popUp.initGui(this.width / 2, this.height / 2);
      }

      int startY = this.height / 2 - 32;
      int distance = 49;
      if (NetworkConfiguration.instance.areBendsPacksAllowed()) {
         this.settingsButton.initGui((this.width - 318) / 2, startY);
         this.packsButton.initGui((this.width - 318) / 2, startY + distance);
         this.customizeButton.initGui((this.width - 318) / 2, startY + distance * 2);
      } else {
         this.settingsButton.initGui((this.width - 318) / 2, startY);
         this.customizeButton.initGui((this.width - 318) / 2, startY + distance);
      }

   }

   protected void keyTyped(char typedChar, int keyCode) {
      if (this.popUp == null) {
         switch(keyCode) {
         case 1:
            goblinbob.mobends.core.util.GuiHelper.closeGui();
         default:
         }
      }
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   public void updateScreen() {
      int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      if (this.popUp != null) {
         this.popUp.update(mouseX, mouseY);
      } else {
         this.settingsButton.update(mouseX, mouseY);
         this.packsButton.update(mouseX, mouseY);
         this.customizeButton.update(mouseX, mouseY);
      }
   }

   protected void mouseClicked(int x, int y, int state) {
      if (this.popUp != null) {
         this.popUp.mouseClicked(x, y, state);
      } else {
         if (this.settingsButton.mouseClicked(x, y, state)) {
            this.mc.displayGuiScreen(new GuiSettingsWindow());
         } else if (this.packsButton.mouseClicked(x, y, state)) {
            this.mc.displayGuiScreen(new GuiPacksWindow());
         } else if (this.customizeButton.mouseClicked(x, y, state)) {
            IAnimationEditor editor = AnimationEditorRegistry.INSTANCE.getPrimaryEditor();
            if (editor == null) {
               this.openPopUp(new GuiEditorNotFound(this::closePopUp, () -> {
                  GuiEditorNotFound editorNotFoundPopup = (GuiEditorNotFound)this.popUp;
                  String downloadUrl = WebAPI.INSTANCE.getOfficialAnimationEditorUrl();
                  if (downloadUrl != null && goblinbob.mobends.core.util.GuiHelper.openUrlInBrowser(downloadUrl)) {
                     this.closePopUp();
                  } else {
                     editorNotFoundPopup.setErrorOccurred(true);
                  }

               }));
            } else {
               editor.openEditorGui();
            }
         }

         try {
            super.mouseClicked(x, y, state);
         } catch (IOException var5) {
            var5.printStackTrace();
         }

      }
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      this.settingsButton.mouseReleased(mouseX, mouseY, state);
      this.packsButton.mouseReleased(mouseX, mouseY, state);
      this.customizeButton.mouseReleased(mouseX, mouseY, state);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      GL11.glDisable(2896);
      GL11.glEnable(3042);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.renderEngine.bindTexture(MENU_TITLE_TEXTURE);
      int titleWidth = 334;
      int titleHeight = 74;
      Draw.texturedRectangle((this.width - titleWidth) / 2, (this.height - titleHeight) / 2 - 70, titleWidth, titleHeight, 0.0F, 0.0F, 1.0F, 1.0F);
      this.settingsButton.display();
      if (NetworkConfiguration.instance.areBendsPacksAllowed()) {
         this.packsButton.display();
      }

      this.customizeButton.display();
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.popUp != null) {
         GlStateManager.disableDepth();
         this.drawDefaultBackground();
         this.popUp.display(mouseX, mouseY, partialTicks);
         GlStateManager.enableDepth();
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   private void closePopUp() {
      this.popUp = null;
      this.initGui();
   }

   private void openPopUp(GuiPopUp popUp) {
      this.popUp = popUp;
      this.initGui();
   }
}
