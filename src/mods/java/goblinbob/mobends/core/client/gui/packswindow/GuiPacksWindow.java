package goblinbob.mobends.core.client.gui.packswindow;

import goblinbob.mobends.core.client.gui.GuiBendsMenu;
import goblinbob.mobends.core.pack.InvalidPackFormatException;
import goblinbob.mobends.core.pack.PackManager;
import goblinbob.mobends.core.util.Draw;
import goblinbob.mobends.core.util.ErrorReporter;
import goblinbob.mobends.core.util.GuiHelper;
import goblinbob.mobends.core.util.Timer;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class GuiPacksWindow extends GuiScreen {
   public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("mobends", "textures/gui/pack_window.png");
   public static final int EDITOR_WIDTH = 280;
   public static final int EDITOR_HEIGHT = 177;
   private static final int BUTTON_BACK = 0;
   private int x;
   private int y;
   private final GuiTabNavigation tabNavigation;
   private final GuiPackTab localPacksTab;
   private final GuiPackTab publicPacksTab;
   private GuiLocalPacks localPacks;
   private Timer timer;

   public GuiPacksWindow() {
      this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
      this.localPacks = new GuiLocalPacks();
      this.tabNavigation = new GuiTabNavigation();
      this.localPacksTab = this.tabNavigation.addTab("mobends.gui.localpacks", 0);
      this.publicPacksTab = this.tabNavigation.addTab("mobends.gui.publicpacks", 1);
      this.tabNavigation.selectTab(0);
      this.timer = new Timer();

      try {
         PackManager.INSTANCE.initLocalPacks();
      } catch (InvalidPackFormatException var2) {
         var2.printStackTrace();
         ErrorReporter.showErrorToPlayer(var2);
      }

   }

   public void onGuiClosed() {
      this.localPacks.dispose();
   }

   public void initGui() {
      super.initGui();
      this.x = (this.width - 280) / 2;
      this.y = (this.height - 177) / 2;
      this.tabNavigation.initGui(this.x + 5, this.y);
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, 10, this.height - 30, 60, 20, I18n.format("mobends.gui.back", new Object[0])));
      this.localPacks.initGui(this.x, this.y);
   }

   public void updateScreen() {
      super.updateScreen();
      int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      this.localPacks.update(mouseX, mouseY);
   }

   public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
      super.mouseClicked(mouseX, mouseY, button);
      this.tabNavigation.mouseClicked(mouseX, mouseY, button);
      this.localPacks.mouseClicked(mouseX, mouseY, button);
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      this.localPacks.mouseReleased(mouseX, mouseY, state);
   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.localPacks.handleMouseInput();
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      float delta = this.timer.tick();
      Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
      Draw.borderBox(this.x + 4, this.y + 4, 280, 177, 4, 36, 126);
      Draw.texturedModalRect(this.x, this.y - 13, 101, 0, 4, 16);
      Draw.texturedModalRect(this.x + 4, this.y - 13, 264, 16, 105, 0, 1, 16);
      Draw.texturedModalRect(this.x + 280 - 17, this.y - 13, 106, 0, 19, 16);
      this.tabNavigation.draw(mouseX, mouseY);
      if (this.tabNavigation.getSelectedTab() == this.localPacksTab) {
         this.localPacks.draw(partialTicks);
      } else if (this.tabNavigation.getSelectedTab() == this.publicPacksTab) {
         this.fontRenderer.drawStringWithShadow("Coming soon...", (float)(this.x + 140 - this.fontRenderer.getStringWidth("Coming soon...") / 2), (float)(this.y + 88 - 10), 16777215);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public void keyTyped(char typedChar, int keyCode) {
      switch(keyCode) {
      case 1:
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
      this.mc.displayGuiScreen(new GuiBendsMenu());
   }
}
