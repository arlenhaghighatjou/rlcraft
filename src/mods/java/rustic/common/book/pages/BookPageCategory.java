package rustic.common.book.pages;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.gui.book.GuiBook;
import rustic.common.book.BookCategory;
import rustic.common.book.BookEntry;
import rustic.common.book.BookEntryCategory;
import rustic.common.book.BookPage;

public class BookPageCategory extends BookPage {
   private final BookCategory category;
   protected List<BookEntry> entries = new ArrayList();

   public BookPageCategory(BookCategory category, BookEntryCategory entry) {
      super(entry);
      this.category = category;
   }

   public void drawScreen(GuiBook gui, int mouseX, int mouseY, float partialTicks) {
      int y = gui.guiTop + 12;
      int x = gui.guiLeft + 73;
      gui.drawCenteredText(TextFormatting.UNDERLINE + "" + TextFormatting.BOLD + I18n.translateToLocal(this.category.getName()), x, y, 0);
   }

   public void onOpened(GuiBook gui) {
      gui.addNavButtons();
      int x = gui.guiLeft + 14;
      int y = gui.guiTop + 24;

      for(BookEntry entry : this.entries) {
         gui.getButtonList().add(new BookPageCategory.EntryLinkButton(gui.nextButtonID(), x, y, 116, gui.getFontRenderer(), entry));
         y += gui.getFontRenderer().FONT_HEIGHT + 2;
      }

   }

   public void onClosed(GuiBook gui) {
      super.onClosed(gui);
   }

   public void actionPerformed(GuiBook gui, GuiButton button) {
      if (button instanceof BookPageCategory.EntryLinkButton) {
         gui.goToEntry(((BookPageCategory.EntryLinkButton)button).getEntry());
      }

   }

   public BookPageCategory addEntry(BookEntry entry) {
      this.entries.add(entry);
      return this;
   }

   @SideOnly(Side.CLIENT)
   public class EntryLinkButton extends GuiButton {
      private int ticksHovered = 0;
      private FontRenderer fontRenderer;
      private final BookEntry entry;

      public EntryLinkButton(int buttonId, int x, int y, int width, FontRenderer fontRenderer, BookEntry entry) {
         super(buttonId, x, y, width, fontRenderer.FONT_HEIGHT + 2, entry.getName());
         this.fontRenderer = fontRenderer;
         this.entry = entry;
      }

      public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
         if (this.visible) {
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (flag) {
               if (this.ticksHovered < this.width) {
                  ++this.ticksHovered;
               }

               int rectWidth = Math.min(this.ticksHovered * 10, this.width);
               Gui.drawRect(this.x, this.y, this.x + rectWidth, this.y + this.height, 1073741824);
            } else {
               this.ticksHovered = 0;
            }

            this.fontRenderer.drawString(I18n.translateToLocal(this.displayString), this.x + this.fontRenderer.FONT_HEIGHT + 2 + 1, this.y + 2, 0);
            if (this.entry != null && !this.entry.getIcon().isEmpty()) {
               float scale = (float)(this.fontRenderer.FONT_HEIGHT + 2) / 16.0F;
               GlStateManager.pushMatrix();
               GlStateManager.scale(scale, scale, scale);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               RenderHelper.enableGUIStandardItemLighting();
               mc.getRenderItem().renderItemIntoGUI(this.entry.getIcon(), (int)((float)this.x / scale), (int)((float)this.y / scale));
               RenderHelper.disableStandardItemLighting();
               GlStateManager.popMatrix();
            }
         }

      }

      public BookEntry getEntry() {
         return this.entry;
      }
   }
}
