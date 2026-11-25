package net.silentchaos512.lib.guidebook.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.gui.TexturedButton;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.IGuideChapter;
import net.silentchaos512.lib.guidebook.IGuidePage;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;
import net.silentchaos512.lib.guidebook.misc.GuideBookUtils;
import net.silentchaos512.lib.guidebook.page.ItemDisplay;

@SideOnly(Side.CLIENT)
public class GuiPage extends GuiGuide {
   public final IGuidePage[] pages = new IGuidePage[2];
   private final List<ItemDisplay> itemDisplays = new ArrayList();
   private int pageTimer;
   private GuiButton buttonViewOnline;

   public GuiPage(GuideBook book, GuiScreen previousScreen, GuiGuideBase parentPage, IGuidePage page1, IGuidePage page2) {
      super(book, previousScreen, parentPage);
      this.pages[0] = page1;
      this.pages[1] = page2;
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);

      for(ItemDisplay display : this.itemDisplays) {
         display.onMousePress(this.book, mouseButton, mouseX, mouseY);
      }

      for(IGuidePage page : this.pages) {
         if (page != null) {
            page.mouseClicked(this, mouseX, mouseY, mouseButton);
         }
      }

   }

   public void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);

      for(IGuidePage page : this.pages) {
         if (page != null) {
            page.mouseReleased(this, mouseX, mouseY, state);
         }
      }

   }

   public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

      for(IGuidePage page : this.pages) {
         if (page != null) {
            page.mouseClickMove(this, mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
         }
      }

   }

   public void actionPerformed(GuiButton button) throws IOException {
      if (button == this.buttonViewOnline) {
         List<String> links = this.getWebLinks();
         if (Desktop.isDesktopSupported()) {
            for(String link : links) {
               try {
                  Desktop.getDesktop().browse(new URI(link));
               } catch (Exception var6) {
                  SilentLib.LOGGER.warn("Couldn't open website from guide book page!");
                  var6.printStackTrace();
               }
            }
         }
      } else {
         super.actionPerformed(button);

         for(IGuidePage page : this.pages) {
            if (page != null) {
               page.actionPerformed(this, button);
            }
         }
      }

   }

   public void initGui() {
      this.itemDisplays.clear();
      super.initGui();
      List<String> links = this.getWebLinks();
      if (links != null && !links.isEmpty()) {
         this.buttonViewOnline = new TexturedButton(this.book.getResourceGadgets(), -782822, this.guiLeft + this.xSize - 24, this.guiTop + this.ySize - 25, 0, 172, 16, 16, Collections.singletonList(TextFormatting.GOLD + this.book.i18n.translate("guide", "onlineButton.name")));
         this.buttonList.add(this.buttonViewOnline);
      }

      for(int i = 0; i < this.pages.length; ++i) {
         IGuidePage page = this.pages[i];
         if (page != null) {
            page.initGui(this, this.guiLeft + 6 + i * 142, this.guiTop + 7);
         }
      }

   }

   private List<String> getWebLinks() {
      List<String> links = new ArrayList();
      return links;
   }

   public void updateScreen() {
      super.updateScreen();

      for(int i = 0; i < this.pages.length; ++i) {
         IGuidePage page = this.pages[i];
         if (page != null) {
            page.updateScreen(this, this.guiLeft + 6 + i * 142, this.guiTop + 7, this.pageTimer);
         }
      }

      ++this.pageTimer;
   }

   public void drawScreenPre(int mouseX, int mouseY, float partialTicks) {
      super.drawScreenPre(mouseX, mouseY, partialTicks);
      if (this.pages[0] != null) {
         IGuideChapter chapter = this.pages[0].getChapter();
         String name = chapter.getLocalizedName();
         this.fontRenderer.drawString(name, (float)(this.guiLeft + this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2), (float)(this.guiTop - 1), 16777215, true);
      }

      for(int i = 0; i < this.pages.length; ++i) {
         IGuidePage page = this.pages[i];
         if (page != null) {
            IGuideChapter chapter = this.pages[i].getChapter();
            String pageStrg = "Page " + (chapter.getPageIndex(this.pages[i]) + 1) + "/" + chapter.getAllPages().length;
            this.renderScaledAsciiString(pageStrg, this.guiLeft + 25 + i * 136, this.guiTop + this.ySize - 7, 16777215, false, this.getLargeFontSize());
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            page.drawScreenPre(this, this.guiLeft + 6 + i * 142, this.guiTop + 7, mouseX, mouseY, partialTicks);
         }
      }

      for(ItemDisplay display : this.itemDisplays) {
         display.drawPre(this.book);
      }

   }

   public void drawScreenPost(int mouseX, int mouseY, float partialTicks) {
      super.drawScreenPost(mouseX, mouseY, partialTicks);

      for(int i = 0; i < this.pages.length; ++i) {
         IGuidePage page = this.pages[i];
         if (page != null) {
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            page.drawScreenPost(this, this.guiLeft + 6 + i * 142, this.guiTop + 7, mouseX, mouseY, partialTicks);
         }
      }

      for(ItemDisplay display : this.itemDisplays) {
         display.drawPost(this.book, mouseX, mouseY);
      }

   }

   public void addOrModifyItemRenderer(ItemStack renderedStack, int x, int y, float scale, boolean shouldTryTransfer) {
      for(ItemDisplay display : this.itemDisplays) {
         if (display.x == x && display.y == y && display.scale == scale) {
            display.stack = renderedStack;
            return;
         }
      }

      this.itemDisplays.add(new ItemDisplay(this.book, this, x, y, scale, renderedStack, shouldTryTransfer));
   }

   public boolean hasPageLeftButton() {
      IGuidePage page = this.pages[0];
      if (page != null) {
         IGuideChapter chapter = page.getChapter();
         if (chapter != null) {
            return chapter.getPageIndex(page) > 0;
         }
      }

      return false;
   }

   public void onPageLeftButtonPressed() {
      IGuidePage page = this.pages[0];
      if (page != null) {
         IGuideChapter chapter = page.getChapter();
         if (chapter != null) {
            IGuidePage[] pages = chapter.getAllPages();
            int pageNumToOpen = chapter.getPageIndex(page) - 1;
            if (pageNumToOpen >= 0 && pageNumToOpen < pages.length) {
               this.mc.displayGuiScreen(GuideBookUtils.createPageGui(this.book, this.previousScreen, this.parentPage, pages[pageNumToOpen]));
            }
         }
      }

   }

   public boolean hasPageRightButton() {
      IGuidePage page = this.pages[1];
      if (page != null) {
         IGuideChapter chapter = page.getChapter();
         if (chapter != null) {
            int pageIndex = chapter.getPageIndex(page);
            int pageAmount = chapter.getAllPages().length;
            return pageIndex + 1 < pageAmount;
         }
      }

      return false;
   }

   public void onPageRightButtonPressed() {
      IGuidePage page = this.pages[1];
      if (page != null) {
         IGuideChapter chapter = page.getChapter();
         if (chapter != null) {
            IGuidePage[] pages = chapter.getAllPages();
            int pageNumToOpen = chapter.getPageIndex(page) + 1;
            if (pageNumToOpen >= 0 && pageNumToOpen < pages.length) {
               this.mc.displayGuiScreen(GuideBookUtils.createPageGui(this.book, this.previousScreen, this.parentPage, pages[pageNumToOpen]));
            }
         }
      }

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
