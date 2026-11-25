package rustic.client.gui.book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import rustic.common.book.BookEntry;
import rustic.common.book.BookEntryCategory;
import rustic.common.book.BookManager;
import rustic.common.book.BookPage;

@SideOnly(Side.CLIENT)
public class GuiBook extends GuiScreen {
   public static final int WIDTH = 146;
   public static final int HEIGHT = 180;
   public static final ResourceLocation BOOK_BACKGROUND = new ResourceLocation("rustic", "textures/gui/book/book.png");
   public int guiLeft;
   public int guiTop;
   private BookEntry currentEntry = null;
   private BookEntry prevEntry = null;
   private int currentPageNum = 0;
   private int prevPageNum = 0;
   private BookPage currentPage = null;
   private GuiBook.BookLocation backLocation = null;
   private List<GuiBook.BookLocation> history = new ArrayList();

   public GuiBook() {
   }

   public void initGui() {
      this.guiTop = (this.height - 180) / 2;
      this.guiLeft = (this.width - 146) / 2;
      if (this.currentEntry == null || this.currentPage == null) {
         this.currentEntry = BookManager.categories_entry;
         this.currentPage = (BookPage)this.currentEntry.getPages().get(this.currentPageNum);
         this.pageOpened();
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawBackgroundLayer(partialTicks, mouseX, mouseY);
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.currentPage.drawScreen(this, mouseX, mouseY, partialTicks);
   }

   protected void drawBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(BOOK_BACKGROUND);
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, 146, 180);
   }

   public void updateScreen() {
      this.prevEntry = this.currentEntry;
      this.prevPageNum = this.currentPageNum;
      if (!this.mc.player.isEntityAlive() || this.mc.player.isDead) {
         this.mc.player.closeScreen();
      }

      this.currentPage.update();
   }

   public void goToPage(int pageNum) {
      if (pageNum >= 0 && this.currentEntry.getPages().size() > pageNum && this.prevPageNum == this.currentPageNum) {
         this.currentPage.onClosed(this);
         this.currentPageNum = pageNum;
         this.currentPage = (BookPage)this.currentEntry.getPages().get(this.currentPageNum);
         this.pageOpened();
      }

   }

   public void goToEntry(BookEntry entry) {
      if (entry != null && entry.getPages().size() > 0 && this.prevEntry == this.currentEntry) {
         if (this.currentEntry instanceof BookEntryCategory && !(entry instanceof BookEntryCategory) && entry != BookManager.categories_entry) {
            this.backLocation = new GuiBook.BookLocation(this.currentEntry, this.currentPageNum);
         }

         if (entry instanceof BookEntryCategory || entry == BookManager.categories_entry) {
            this.backLocation = null;
         }

         if (!(entry instanceof BookEntryCategory) && entry != BookManager.categories_entry) {
            if (!(this.currentEntry instanceof BookEntryCategory) && this.currentEntry != BookManager.categories_entry) {
               this.history.add(new GuiBook.BookLocation(this.currentEntry, this.currentPageNum));
            }
         } else {
            this.history.clear();
         }

         this.currentPage.onClosed(this);
         this.currentEntry = entry;
         this.currentPageNum = 0;
         this.currentPage = (BookPage)this.currentEntry.getPages().get(this.currentPageNum);
         this.pageOpened();
      }

   }

   private void goToHistoryLocation(int index) {
      if (index >= 0 && index < this.history.size() && this.prevEntry == this.currentEntry && this.prevPageNum == this.currentPageNum) {
         BookEntry entry = ((GuiBook.BookLocation)this.history.get(index)).getEntry();
         int page = ((GuiBook.BookLocation)this.history.get(index)).getPage();
         int i = index;

         while(i < this.history.size()) {
            this.history.remove(i);
         }

         this.currentPage.onClosed(this);
         this.currentEntry = entry;
         this.currentPageNum = page;
         this.currentPage = (BookPage)this.currentEntry.getPages().get(this.currentPageNum);
         this.pageOpened();
      }

   }

   @SideOnly(Side.CLIENT)
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button instanceof GuiBook.PageTurnButton) {
         if (((GuiBook.PageTurnButton)button).isForward()) {
            this.goToPage(this.currentPageNum + 1);
         } else {
            this.goToPage(this.currentPageNum - 1);
         }
      } else if (button instanceof GuiBook.BackButton) {
         if (this.currentEntry.getCategory() == null) {
            this.goToEntry(BookManager.categories_entry);
         } else if (this.backLocation != null) {
            this.backLocation.goTo(this);
         } else {
            this.goToEntry(this.currentEntry.getCategory().getCategoryEntry());
         }
      } else if (button instanceof GuiBook.LastHistoryButton) {
         this.goToHistoryLocation(this.history.size() - 1);
      } else {
         this.currentPage.actionPerformed(this, button);
      }

   }

   protected void keyTyped(char c, int key) throws IOException {
      this.currentPage.keyPressed(c, key);
      if (this.mc.gameSettings.keyBindInventory.getKeyCode() == key) {
         if (this.currentEntry == BookManager.categories_entry) {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
         } else if (this.currentEntry.getCategory() == null) {
            this.goToEntry(BookManager.categories_entry);
         } else if (this.backLocation != null) {
            this.backLocation.goTo(this);
         } else {
            this.goToEntry(this.currentEntry.getCategory().getCategoryEntry());
         }
      } else if (1 == key) {
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
      }

   }

   public void onGuiClosed() {
      this.currentPage.onClosed(this);
   }

   protected void pageOpened() {
      this.currentPage.onOpened(this);
   }

   public void addNavButtons() {
      int numPages = this.currentEntry.getPages().size();
      if (this.currentPageNum > 0) {
         this.addButton(new GuiBook.PageTurnButton(this.buttonList.size(), this.guiLeft, this.guiTop + 180 - 10, false));
      }

      if (this.currentPageNum < numPages - 1) {
         this.addButton(new GuiBook.PageTurnButton(this.nextButtonID(), this.guiLeft + 146 - 18, this.guiTop + 180 - 10, true));
      }

      boolean backButton = this.currentEntry != BookManager.categories_entry;
      boolean histButton = this.history.size() > 0 && this.currentEntry != BookManager.categories_entry;
      if (backButton) {
         this.addButton(new GuiBook.BackButton(this.nextButtonID(), histButton ? this.guiLeft + 73 - 15 : this.guiLeft + 73 - 8, this.guiTop + 180 + 0));
      }

      if (histButton) {
         this.addButton(new GuiBook.LastHistoryButton(this.nextButtonID(), backButton ? this.guiLeft + 73 + 15 - 10 : this.guiLeft + 73 - 5, this.guiTop + 180 - 3));
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void onResize(Minecraft mcIn, int w, int h) {
      this.setWorldAndResolution(mcIn, w, h);
      this.currentPage.onClosed(this);
      this.pageOpened();
   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   public List<GuiButton> getButtonList() {
      return this.buttonList;
   }

   public int nextButtonID() {
      return this.buttonList.size();
   }

   public void drawCenteredText(String text, int x, int y, int color) {
      int cX = x - this.fontRenderer.getStringWidth(text) / 2;
      this.fontRenderer.drawString(text, cX, y, color);
   }

   public void drawHorizontalLine(int startX, int endX, int y, int color) {
      if (endX < startX) {
         int i = startX;
         startX = endX;
         endX = i;
      }

      drawRect(startX, y, endX + 1, y + 1, color);
   }

   public void drawVerticalLine(int x, int startY, int endY, int color) {
      if (endY < startY) {
         int i = startY;
         startY = endY;
         endY = i;
      }

      drawRect(x, startY + 1, x + 1, endY, color);
   }

   public void renderToolTip(ItemStack stack, int x, int y) {
      super.renderToolTip(stack, x, y);
   }

   @SideOnly(Side.CLIENT)
   public class BackButton extends GuiButton {
      public BackButton(int buttonId, int x, int y) {
         super(buttonId, x, y, 16, 10, "book.rustic.button.back");
      }

      public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
         if (this.visible) {
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(GuiBook.BOOK_BACKGROUND);
            int i = 0;
            int j = 200;
            if (flag) {
               i += 16;
            }

            this.drawTexturedModalRect(this.x, this.y, i, j, 16, 10);
         }

      }
   }

   private class BookLocation {
      private BookEntry entry;
      private int page;

      public BookLocation(BookEntry entry, int page) {
         this.entry = entry;
         this.page = page;
      }

      public void goTo(GuiBook gui) {
         gui.goToEntry(this.entry);
         gui.goToPage(this.page);
      }

      public BookEntry getEntry() {
         return this.entry;
      }

      public int getPage() {
         return this.page;
      }
   }

   @SideOnly(Side.CLIENT)
   public class LastHistoryButton extends GuiButton {
      public LastHistoryButton(int buttonId, int x, int y) {
         super(buttonId, x, y, 10, 16, "book.rustic.button.last_history");
      }

      public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
         if (this.visible) {
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(GuiBook.BOOK_BACKGROUND);
            int i = 36;
            int j = 193;
            if (flag) {
               i += 10;
            }

            this.drawTexturedModalRect(this.x, this.y, i, j, 10, 16);
         }

      }
   }

   @SideOnly(Side.CLIENT)
   public class PageTurnButton extends GuiButton {
      private final boolean isForward;

      public PageTurnButton(int buttonId, int x, int y, boolean isForward) {
         super(buttonId, x, y, 18, 10, "book.rustic.button." + (isForward ? "next" : "prev"));
         this.isForward = isForward;
      }

      public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
         if (this.visible) {
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(GuiBook.BOOK_BACKGROUND);
            int i = 0;
            int j = 180;
            if (flag) {
               i += 18;
            }

            if (!this.isForward) {
               j += 10;
            }

            this.drawTexturedModalRect(this.x, this.y, i, j, 18, 10);
         }

      }

      public boolean isForward() {
         return this.isForward;
      }
   }
}
