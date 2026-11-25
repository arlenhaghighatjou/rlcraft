package net.silentchaos512.lib.guidebook.gui;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuideChapter;
import net.silentchaos512.lib.guidebook.IGuideEntry;
import net.silentchaos512.lib.guidebook.IGuidePage;
import net.silentchaos512.lib.guidebook.button.EntryButton;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;
import net.silentchaos512.lib.guidebook.misc.GuideBookUtils;

@SideOnly(Side.CLIENT)
public class GuiEntry extends GuiGuide {
   private final int entryPage;
   private final int pageAmount;
   private final IGuideEntry entry;
   private final List<IGuideChapter> chapters;
   private final String searchText;
   private final boolean focusSearch;
   protected GuideBook book;

   public GuiEntry(GuideBook book, GuiScreen previousScreen, GuiGuideBase parentPage, IGuideEntry entry, int entryPage, String search, boolean focusSearch) {
      super(book, previousScreen, parentPage);
      this.entryPage = entryPage;
      this.entry = entry;
      this.searchText = search;
      this.focusSearch = focusSearch;
      this.chapters = entry.getChaptersForDisplay(search);
      if (!this.chapters.isEmpty()) {
         IGuideChapter lastChap = (IGuideChapter)this.chapters.get(this.chapters.size() - 1);
         this.pageAmount = lastChap == null ? 1 : calcEntryPage(this.entry, lastChap, this.searchText) + 1;
      } else {
         this.pageAmount = 1;
      }

   }

   public GuiEntry(GuideBook book, GuiScreen previousScreen, GuiGuideBase parentPage, IGuideEntry entry, IGuideChapter chapterForPageCalc, String search, boolean focusSearch) {
      this(book, previousScreen, parentPage, entry, calcEntryPage(entry, chapterForPageCalc, search), search, focusSearch);
   }

   private static int calcEntryPage(IGuideEntry entry, IGuideChapter chapterForPageCalc, String search) {
      int index = entry.getChaptersForDisplay(search).indexOf(chapterForPageCalc);
      return index / 24;
   }

   public void drawScreenPre(int mouseX, int mouseY, float partialTicks) {
      super.drawScreenPre(mouseX, mouseY, partialTicks);
      String name = this.entry.getLocalizedName();
      this.fontRenderer.drawString(name, (float)(this.guiLeft + this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2), (float)(this.guiTop - 1), 16777215, true);

      for(int i = 0; i < 2; ++i) {
         String pageStrg = "Page " + (this.entryPage * 2 + i + 1) + "/" + this.pageAmount * 2;
         this.renderScaledAsciiString(pageStrg, this.guiLeft + 25 + i * 136, this.guiTop + this.ySize - 7, 16777215, false, this.getLargeFontSize());
      }

   }

   public void initGui() {
      super.initGui();
      if (this.hasSearchBar() && this.searchText != null) {
         this.searchField.setText(this.searchText);
         if (this.focusSearch) {
            this.searchField.setFocused(true);
         }
      }

      int idOffset = this.entryPage * 24;

      for(int x = 0; x < 2; ++x) {
         for(int y = 0; y < 12; ++y) {
            int id = y + x * 12;
            if (this.chapters.size() <= id + idOffset) {
               return;
            }

            IGuideChapter chapter = (IGuideChapter)this.chapters.get(id + idOffset);
            this.buttonList.add(new EntryButton(this, id, this.guiLeft + 14 + x * 142, this.guiTop + 11 + y * 13, 115, 10, chapter.getLocalizedNameWithFormatting(), chapter.getDisplayItemStack()));
         }
      }

   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if (button instanceof EntryButton) {
         int actualId = button.id + this.entryPage * 24;
         if (this.chapters.size() > actualId) {
            IGuideChapter chapter = (IGuideChapter)this.chapters.get(actualId);
            if (chapter != null) {
               IGuidePage[] pages = chapter.getAllPages();
               if (pages != null && pages.length > 0) {
                  this.mc.displayGuiScreen(GuideBookUtils.createPageGui(this.book, this.previousScreen, this, pages[0]));
               }
            }
         }
      } else {
         super.actionPerformed(button);
      }

   }

   public void addOrModifyItemRenderer(ItemStack renderedStack, int x, int y, float scale, boolean shouldTryTransfer) {
   }

   public boolean hasPageLeftButton() {
      return this.entryPage > 0;
   }

   public void onPageLeftButtonPressed() {
      this.mc.displayGuiScreen(new GuiEntry(this.book, this.previousScreen, this.parentPage, this.entry, this.entryPage - 1, this.searchText, this.searchField.isFocused()));
   }

   public boolean hasPageRightButton() {
      return !this.chapters.isEmpty() && this.entryPage < this.pageAmount - 1;
   }

   public void onPageRightButtonPressed() {
      this.mc.displayGuiScreen(new GuiEntry(this.book, this.previousScreen, this.parentPage, this.entry, this.entryPage + 1, this.searchText, this.searchField.isFocused()));
   }

   public boolean hasBackButton() {
      return true;
   }

   public void onBackButtonPressed() {
      if (!isShiftKeyDown()) {
         this.mc.displayGuiScreen(this.parentPage);
      } else {
         super.onBackButtonPressed();
      }

   }
}
