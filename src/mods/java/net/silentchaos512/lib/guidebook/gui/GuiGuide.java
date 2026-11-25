package net.silentchaos512.lib.guidebook.gui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.gui.TexturedButton;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.button.BookmarkButton;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;
import net.silentchaos512.lib.guidebook.misc.GuideBookUtils;
import net.silentchaos512.lib.util.StringUtil;
import org.apache.commons.lang3.ArrayUtils;
import net.lax1dude.eaglercraft.v1_8.Mouse;

@SideOnly(Side.CLIENT)
public abstract class GuiGuide extends GuiGuideBase {
   public static final int BUTTONS_PER_PAGE = 12;
   protected final BookmarkButton[] bookmarkButtons = new BookmarkButton[12];
   public GuiScreen previousScreen;
   public GuiGuideBase parentPage;
   public GuiTextField searchField;
   public GuideBook book;
   protected int xSize;
   protected int ySize;
   protected int guiLeft;
   protected int guiTop;
   private GuiButton buttonLeft;
   private GuiButton buttonRight;
   private GuiButton buttonBack;
   private float smallFontSize = 0.5F;
   private float mediumFontSize = 0.75F;
   private float largeFontSize = 0.8F;

   public GuiGuide(GuideBook book, GuiScreen previousScreen, GuiGuideBase parentPage) {
      this.book = book;
      this.previousScreen = previousScreen;
      this.parentPage = parentPage;
      this.xSize = 281;
      this.ySize = 180;
   }

   public void initGui() {
      super.initGui();
      if (this.book == null) {
         this.book = GuideBookUtils.getBookFromClientPlayerHand();
      }

      this.book.lastViewedPage = this;
      this.guiLeft = (this.width - this.xSize) / 2;
      this.guiTop = (this.height - this.ySize) / 2;
      if (this.hasPageLeftButton()) {
         List<String> hoverText = Arrays.asList(TextFormatting.GOLD + "Previous Page", TextFormatting.ITALIC + "Or scroll up");
         this.buttonLeft = new TexturedButton(this.book.getResourceGadgets(), -2000, this.guiLeft - 12, this.guiTop + this.ySize - 8, 18, 54, 18, 10, hoverText);
         this.buttonList.add(this.buttonLeft);
      }

      if (this.hasPageRightButton()) {
         List<String> hoverText = Arrays.asList(TextFormatting.GOLD + "Next Page", TextFormatting.ITALIC + "Or scroll down");
         this.buttonRight = new TexturedButton(this.book.getResourceGadgets(), -2001, this.guiLeft + this.xSize - 6, this.guiTop + this.ySize - 8, 0, 54, 18, 10, hoverText);
         this.buttonList.add(this.buttonRight);
      }

      if (this.hasBackButton()) {
         List<String> hoverText = Arrays.asList(TextFormatting.GOLD + "Go Back", TextFormatting.ITALIC + "Or right-click", TextFormatting.ITALIC.toString() + TextFormatting.GRAY + "Hold Shift for Main Page");
         this.buttonBack = new TexturedButton(this.book.getResourceGadgets(), -2002, this.guiLeft - 15, this.guiTop - 3, 36, 54, 18, 10, hoverText);
         this.buttonList.add(this.buttonBack);
      }

      if (this.hasSearchBar()) {
         this.searchField = new GuiTextField(-420, this.fontRenderer, this.guiLeft + this.xSize + 2, this.guiTop + this.ySize - 40 + 2, 64, 12);
         this.searchField.setMaxStringLength(50);
         this.searchField.setEnableBackgroundDrawing(false);
      }

   }

   public void onGuiClosed() {
      super.onGuiClosed();
      this.previousScreen = null;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawScreenPre(mouseX, mouseY, partialTicks);
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.drawScreenPost(mouseX, mouseY, partialTicks);
   }

   public void drawScreenPre(int mouseX, int mouseY, float partialTicks) {
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(this.book.getResourceGui());
      drawModalRectWithCustomSizedTexture(this.guiLeft, this.guiTop, 0.0F, 0.0F, this.xSize, this.ySize, 512.0F, 512.0F);
      if (this.hasSearchBar()) {
         this.mc.getTextureManager().bindTexture(this.book.getResourceGadgets());
         this.drawTexturedModalRect(this.guiLeft + this.xSize, this.guiTop + this.ySize - 40, 188, 0, 68, 14);
         boolean unicodeBefore = this.fontRenderer.getUnicodeFlag();
         this.fontRenderer.setUnicodeFlag(true);
         if (!this.searchField.isFocused() && (this.searchField.getText() == null || this.searchField.getText().isEmpty())) {
            String line = this.book.i18n.translate("guide.silentlib:searchField");
            this.fontRenderer.drawString(TextFormatting.ITALIC + line, (float)(this.guiLeft + this.xSize + 2), (float)(this.guiTop + this.ySize - 40 + 2), 16777215, false);
         }

         this.searchField.drawTextBox();
         this.fontRenderer.setUnicodeFlag(unicodeBefore);
      }

   }

   public void drawScreenPost(int mouseX, int mouseY, float partialTicks) {
      for(GuiButton button : this.buttonList) {
         if (button instanceof BookmarkButton) {
            ((BookmarkButton)button).drawHover(mouseX, mouseY);
         } else if (button instanceof TexturedButton) {
            ((TexturedButton)button).drawHover(mouseX, mouseY);
         }
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.hasSearchBar()) {
         this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
      }

      if (mouseButton == 1 && this.hasBackButton()) {
         this.onBackButtonPressed();
      }

   }

   public void handleMouseInput() throws IOException {
      int wheel = Mouse.getEventDWheel();
      if (wheel != 0) {
         if (wheel < 0) {
            if (this.hasPageRightButton()) {
               this.onPageRightButtonPressed();
            }
         } else if (wheel > 0 && this.hasPageLeftButton()) {
            this.onPageLeftButtonPressed();
         }
      }

      super.handleMouseInput();
   }

   public void updateScreen() {
      super.updateScreen();
      if (this.hasSearchBar()) {
         this.searchField.updateCursorCounter();
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public boolean hasPageLeftButton() {
      return false;
   }

   public void onPageLeftButtonPressed() {
   }

   public boolean hasPageRightButton() {
      return false;
   }

   public void onPageRightButtonPressed() {
   }

   public boolean hasBackButton() {
      return false;
   }

   public void onBackButtonPressed() {
      this.mc.displayGuiScreen(new GuiMainPage(this.book, this.previousScreen));
   }

   public boolean hasSearchBar() {
      return true;
   }

   public boolean hasBookmarkButtons() {
      return true;
   }

   public float getSmallFontSize() {
      return this.smallFontSize;
   }

   public float getMediumFontSize() {
      return this.mediumFontSize;
   }

   public float getLargeFontSize() {
      return this.largeFontSize;
   }

   public void onSearchBarChanged(String searchBarText) {
      GuiGuideBase parent = (GuiGuideBase)(!(this instanceof GuiEntry) ? this : this.parentPage);
      this.mc.displayGuiScreen(new GuiEntry(this.book, this.previousScreen, parent, this.book.entryAllAndSearch, 0, searchBarText, true));
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if (this.hasPageLeftButton() && button == this.buttonLeft) {
         this.onPageLeftButtonPressed();
      } else if (this.hasPageRightButton() && button == this.buttonRight) {
         this.onPageRightButtonPressed();
      } else if (this.hasBackButton() && button == this.buttonBack) {
         this.onBackButtonPressed();
      }

      if (this.hasBookmarkButtons() && button instanceof BookmarkButton) {
         int index = ArrayUtils.indexOf(this.bookmarkButtons, button);
         if (index >= 0) {
            this.bookmarkButtons[index].onPressed();
         }
      } else {
         super.actionPerformed(button);
      }

   }

   protected void keyTyped(char typedChar, int key) throws IOException {
      if (key != 1 && (key != this.mc.gameSettings.keyBindInventory.getKeyCode() || this.hasSearchBar() && this.searchField.isFocused())) {
         if (this.hasSearchBar() & this.searchField.isFocused()) {
            String lastText = this.searchField.getText();
            this.searchField.textboxKeyTyped(typedChar, key);
            if (!lastText.equals(this.searchField.getText())) {
               this.onSearchBarChanged(this.searchField.getText());
            }
         } else {
            super.keyTyped(typedChar, key);
         }
      } else {
         this.mc.displayGuiScreen(this.previousScreen);
      }

   }

   public void renderScaledAsciiString(String text, int x, int y, int color, boolean shadow, float scale) {
      StringUtil.renderScaledAsciiString(this.fontRenderer, text, x, y, color, shadow, scale);
   }

   public void renderSplitScaledAsciiString(String text, int x, int y, int color, boolean shadow, float scale, int length) {
      StringUtil.renderSplitScaledAsciiString(this.fontRenderer, text, x, y, color, shadow, scale, length);
   }

   public List<GuiButton> getButtonList() {
      return this.buttonList;
   }

   public int getGuiLeft() {
      return this.guiLeft;
   }

   public int getGuiTop() {
      return this.guiTop;
   }

   public int getSizeX() {
      return this.xSize;
   }

   public int getSizeY() {
      return this.ySize;
   }
}
