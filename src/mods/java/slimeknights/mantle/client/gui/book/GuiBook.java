package slimeknights.mantle.client.gui.book;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ClientAdvancementManager.IListener;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import slimeknights.mantle.client.book.BookHelper;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.action.StringActionProcessor;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.element.ItemStackData;
import slimeknights.mantle.client.gui.book.element.BookElement;

@SideOnly(Side.CLIENT)
public class GuiBook extends GuiScreen {
   public static boolean debug = false;
   public static final int TEX_SIZE = 512;
   public static int PAGE_MARGIN = 8;
   public static int PAGE_PADDING_TOP = 4;
   public static int PAGE_PADDING_BOT = 4;
   public static int PAGE_PADDING_LEFT = 8;
   public static int PAGE_PADDING_RIGHT = 0;
   public static float PAGE_SCALE = 1.0F;
   public static int PAGE_WIDTH_UNSCALED = 206;
   public static int PAGE_HEIGHT_UNSCALED = 200;
   public static int PAGE_WIDTH;
   public static int PAGE_HEIGHT;
   private GuiArrow previousArrow;
   private GuiArrow nextArrow;
   private GuiArrow backArrow;
   private GuiArrow indexArrow;
   public final BookData book;
   private ItemStack item;
   private int page = -1;
   private int oldPage = -2;
   private ArrayList<BookElement> leftElements = new ArrayList();
   private ArrayList<BookElement> rightElements = new ArrayList();
   public GuiBook.AdvancementCache advancementCache;

   public static void init() {
      PAGE_WIDTH = (int)((float)(PAGE_WIDTH_UNSCALED - (PAGE_PADDING_LEFT + PAGE_PADDING_RIGHT + PAGE_MARGIN + PAGE_MARGIN)) / PAGE_SCALE);
      PAGE_HEIGHT = (int)((float)(PAGE_HEIGHT_UNSCALED - (PAGE_PADDING_TOP + PAGE_PADDING_BOT + PAGE_MARGIN + PAGE_MARGIN)) / PAGE_SCALE);
   }

   public GuiBook(BookData book, @Nullable ItemStack item) {
      this.book = book;
      this.item = item;
      this.mc = Minecraft.getMinecraft();
      this.fontRenderer = this.mc.fontRenderer;
      init();
      this.advancementCache = new GuiBook.AdvancementCache();
      this.mc.player.connection.getAdvancementManager().setListener(this.advancementCache);
      this.openPage(book.findPageNumber(BookHelper.getSavedPage(item), this.advancementCache));
   }

   public void drawerTransform(boolean rightSide) {
      if (rightSide) {
         GlStateManager.translate((float)(this.width / 2 + PAGE_PADDING_RIGHT + PAGE_MARGIN), (float)(this.height / 2 - PAGE_HEIGHT_UNSCALED / 2 + PAGE_PADDING_TOP + PAGE_MARGIN), 0.0F);
      } else {
         GlStateManager.translate((float)(this.width / 2 - PAGE_WIDTH_UNSCALED + PAGE_PADDING_LEFT + PAGE_MARGIN), (float)(this.height / 2 - PAGE_HEIGHT_UNSCALED / 2 + PAGE_PADDING_TOP + PAGE_MARGIN), 0.0F);
      }

   }

   protected float leftOffset(boolean rightSide) {
      return rightSide ? (float)(this.width / 2 + PAGE_PADDING_RIGHT + PAGE_MARGIN) : (float)(this.width / 2 - PAGE_WIDTH_UNSCALED + PAGE_PADDING_LEFT + PAGE_MARGIN);
   }

   protected float topOffset() {
      return (float)(this.height / 2 - PAGE_HEIGHT_UNSCALED / 2 + PAGE_PADDING_TOP + PAGE_MARGIN);
   }

   protected int getMouseX(boolean rightSide) {
      return (int)(((float)(Mouse.getX() * this.width / this.mc.displayWidth) - this.leftOffset(rightSide)) / PAGE_SCALE);
   }

   protected int getMouseY() {
      return (int)(((float)(this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1) - this.topOffset()) / PAGE_SCALE);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      init();
      FontRenderer fontRenderer = this.book.fontRenderer;
      if (fontRenderer == null) {
         fontRenderer = this.mc.fontRenderer;
      }

      if (debug) {
         drawRect(0, 0, fontRenderer.getStringWidth("DEBUG") + 4, fontRenderer.FONT_HEIGHT + 4, 1426063360);
         fontRenderer.drawString("DEBUG", 2, 2, -1);
      }

      GlStateManager.enableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      float coverR = (float)(this.book.appearance.coverColor >> 16 & 255) / 255.0F;
      float coverG = (float)(this.book.appearance.coverColor >> 8 & 255) / 255.0F;
      float coverB = (float)(this.book.appearance.coverColor & 255) / 255.0F;
      TextureManager render = this.mc.renderEngine;
      if (this.page == -1) {
         render.bindTexture(Textures.TEX_BOOKFRONT);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.color(coverR, coverG, coverB);
         drawModalRectWithCustomSizedTexture(this.width / 2 - PAGE_WIDTH_UNSCALED / 2, this.height / 2 - PAGE_HEIGHT_UNSCALED / 2, 0.0F, 0.0F, PAGE_WIDTH_UNSCALED, PAGE_HEIGHT_UNSCALED, 512.0F, 512.0F);
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         if (!this.book.appearance.title.isEmpty()) {
            drawModalRectWithCustomSizedTexture(this.width / 2 - PAGE_WIDTH_UNSCALED / 2, this.height / 2 - PAGE_HEIGHT_UNSCALED / 2, 0.0F, (float)PAGE_HEIGHT_UNSCALED, PAGE_WIDTH_UNSCALED, PAGE_HEIGHT_UNSCALED, 512.0F, 512.0F);
            GlStateManager.pushMatrix();
            float scale = fontRenderer.getStringWidth(this.book.appearance.title) <= 67 ? 2.5F : 2.0F;
            GlStateManager.scale(scale, scale, 1.0F);
            fontRenderer.drawString(this.book.appearance.title, (float)(this.width / 2) / scale + 3.0F - (float)(fontRenderer.getStringWidth(this.book.appearance.title) / 2), (float)(this.height / 2 - fontRenderer.FONT_HEIGHT / 2) / scale - 4.0F, 11436032, true);
            GlStateManager.popMatrix();
         }

         if (!this.book.appearance.subtitle.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.5F, 1.5F, 1.0F);
            fontRenderer.drawString(this.book.appearance.subtitle, (float)(this.width / 2) / 1.5F + 7.0F - (float)(fontRenderer.getStringWidth(this.book.appearance.subtitle) / 2), (float)(this.height / 2 + 100 - fontRenderer.FONT_HEIGHT * 2) / 1.5F, 11436032, true);
            GlStateManager.popMatrix();
         }
      } else {
         render.bindTexture(Textures.TEX_BOOK);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.color(coverR, coverG, coverB);
         drawModalRectWithCustomSizedTexture(this.width / 2 - PAGE_WIDTH_UNSCALED, this.height / 2 - PAGE_HEIGHT_UNSCALED / 2, 0.0F, 0.0F, PAGE_WIDTH_UNSCALED * 2, PAGE_HEIGHT_UNSCALED, 512.0F, 512.0F);
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         if (this.page != 0) {
            drawModalRectWithCustomSizedTexture(this.width / 2 - PAGE_WIDTH_UNSCALED, this.height / 2 - PAGE_HEIGHT_UNSCALED / 2, 0.0F, (float)PAGE_HEIGHT_UNSCALED, PAGE_WIDTH_UNSCALED, PAGE_HEIGHT_UNSCALED, 512.0F, 512.0F);
            GlStateManager.pushMatrix();
            this.drawerTransform(false);
            GlStateManager.scale(PAGE_SCALE, PAGE_SCALE, 1.0F);
            if (this.book.appearance.drawPageNumbers) {
               String pNum = (this.page - 1) * 2 + 2 + "";
               fontRenderer.drawString(pNum, (float)(PAGE_WIDTH / 2 - fontRenderer.getStringWidth(pNum) / 2), (float)(PAGE_HEIGHT - 10), -5592406, false);
            }

            int mX = this.getMouseX(false);
            int mY = this.getMouseY();

            for(int i = 0; i < this.leftElements.size(); ++i) {
               BookElement element = (BookElement)this.leftElements.get(i);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               element.draw(mX, mY, partialTicks, fontRenderer);
            }

            for(int i = 0; i < this.leftElements.size(); ++i) {
               BookElement element = (BookElement)this.leftElements.get(i);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               element.drawOverlay(mX, mY, partialTicks, fontRenderer);
            }

            GlStateManager.popMatrix();
         }

         render.bindTexture(Textures.TEX_BOOK);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         RenderHelper.disableStandardItemLighting();
         int fullPageCount = this.book.getFullPageCount(this.advancementCache);
         if (this.page < fullPageCount - 1 || this.book.getPageCount(this.advancementCache) % 2 != 0) {
            drawModalRectWithCustomSizedTexture(this.width / 2, this.height / 2 - PAGE_HEIGHT_UNSCALED / 2, (float)PAGE_WIDTH_UNSCALED, (float)PAGE_HEIGHT_UNSCALED, PAGE_WIDTH_UNSCALED, PAGE_HEIGHT_UNSCALED, 512.0F, 512.0F);
            GlStateManager.pushMatrix();
            this.drawerTransform(true);
            GlStateManager.scale(PAGE_SCALE, PAGE_SCALE, 1.0F);
            if (this.book.appearance.drawPageNumbers) {
               String pNum = (this.page - 1) * 2 + 3 + "";
               fontRenderer.drawString(pNum, (float)(PAGE_WIDTH / 2 - fontRenderer.getStringWidth(pNum) / 2), (float)(PAGE_HEIGHT - 10), -5592406, false);
            }

            int mX = this.getMouseX(true);
            int mY = this.getMouseY();

            for(int i = 0; i < this.rightElements.size(); ++i) {
               BookElement element = (BookElement)this.rightElements.get(i);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               element.draw(mX, mY, partialTicks, fontRenderer);
            }

            for(int i = 0; i < this.rightElements.size(); ++i) {
               BookElement element = (BookElement)this.rightElements.get(i);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               element.drawOverlay(mX, mY, partialTicks, fontRenderer);
            }

            GlStateManager.popMatrix();
         }
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
      GlStateManager.popMatrix();
   }

   public int openPage(int page) {
      return this.openPage(page, false);
   }

   public int openPage(int page, boolean returner) {
      if (page < 0) {
         return -1;
      } else {
         int bookPage;
         if (page == 1) {
            bookPage = 0;
         } else if (page % 2 == 0) {
            bookPage = (page - 1) / 2 + 1;
         } else {
            bookPage = (page - 2) / 2 + 1;
         }

         if (bookPage >= -1 && bookPage < this.book.getFullPageCount(this.advancementCache)) {
            if (returner) {
               this.oldPage = this.page;
            }

            this._setPage(bookPage);
         }

         return page % 2 == 0 ? 0 : 1;
      }
   }

   public void _setPage(int page) {
      this.page = page;
      this.buildPages();
   }

   public int getPage(int side) {
      if (this.page == 0 && side == 0) {
         return -1;
      } else if (this.page == 0 && side == 1) {
         return 0;
      } else if (side == 0) {
         return (this.page - 1) * 2 + 1;
      } else {
         return side == 1 ? (this.page - 2) * 2 + 2 : -1;
      }
   }

   public int getPage_() {
      return this.page;
   }

   public ArrayList<BookElement> getElements(int side) {
      return side == 0 ? this.leftElements : (side == 1 ? this.rightElements : null);
   }

   public void openCover() {
      this._setPage(-1);
      this.leftElements.clear();
      this.rightElements.clear();
      this.buildPages();
   }

   public void itemClicked(ItemStack item) {
      StringActionProcessor.process(this.book.getItemAction(ItemStackData.getItemStackData(item, true)), this);
   }

   private void buildPages() {
      this.leftElements.clear();
      this.rightElements.clear();
      if (this.page != -1) {
         if (this.page == 0) {
            PageData page = this.book.findPage(0, this.advancementCache);
            if (page != null) {
               page.content.build(this.book, this.rightElements, false);
            }
         } else {
            PageData leftPage = this.book.findPage((this.page - 1) * 2 + 1, this.advancementCache);
            PageData rightPage = this.book.findPage((this.page - 1) * 2 + 2, this.advancementCache);
            if (leftPage != null) {
               leftPage.content.build(this.book, this.leftElements, false);
            }

            if (rightPage != null) {
               rightPage.content.build(this.book, this.rightElements, true);
            }
         }

         for(BookElement element : this.leftElements) {
            element.parent = this;
         }

         for(BookElement element : this.rightElements) {
            element.parent = this;
         }

      }
   }

   public void initGui() {
      super.initGui();
      this.previousArrow = new GuiArrow(0, -50, -50, GuiArrow.ArrowType.PREV, this.book.appearance.arrowColor, this.book.appearance.arrowColorHover);
      this.nextArrow = new GuiArrow(1, -50, -50, GuiArrow.ArrowType.NEXT, this.book.appearance.arrowColor, this.book.appearance.arrowColorHover);
      this.backArrow = new GuiArrow(2, this.width / 2 - 9, this.height / 2 + 5 + PAGE_HEIGHT / 2, GuiArrow.ArrowType.LEFT, this.book.appearance.arrowColor, this.book.appearance.arrowColorHover);
      this.indexArrow = new GuiArrow(3, this.width / 2 - PAGE_WIDTH_UNSCALED - 9, this.height / 2 - PAGE_HEIGHT_UNSCALED / 2, GuiArrow.ArrowType.BACK_UP, this.book.appearance.arrowColor, this.book.appearance.arrowColorHover);
      this.buttonList.clear();
      this.buttonList.add(this.previousArrow);
      this.buttonList.add(this.nextArrow);
      this.buttonList.add(this.backArrow);
      this.buttonList.add(this.indexArrow);
      this.buildPages();
   }

   public void updateScreen() {
      super.updateScreen();
      this.previousArrow.visible = this.page != -1;
      this.nextArrow.visible = this.page + 1 < this.book.getFullPageCount(this.advancementCache);
      this.backArrow.visible = this.oldPage >= -1;
      if (this.page == -1) {
         this.nextArrow.x = this.width / 2 + 80;
         this.indexArrow.visible = false;
      } else {
         this.previousArrow.x = this.width / 2 - 184;
         this.nextArrow.x = this.width / 2 + 165;
         this.indexArrow.visible = this.book.findSection("index") != null && (this.page - 1) * 2 + 2 > this.book.findSection("index").getPageCount();
      }

      this.previousArrow.y = this.height / 2 + 75;
      this.nextArrow.y = this.height / 2 + 75;
   }

   public void actionPerformed(GuiButton button) {
      if (button instanceof GuiBookmark) {
         this.openPage(this.book.findPageNumber(((GuiBookmark)button).data.page, this.advancementCache));
      } else {
         if (button == this.previousArrow) {
            --this.page;
            if (this.page < -1) {
               this.page = -1;
            }
         } else if (button == this.nextArrow) {
            ++this.page;
            int fullPageCount = this.book.getFullPageCount(this.advancementCache);
            if (this.page >= fullPageCount) {
               this.page = fullPageCount - 1;
            }
         } else if (button == this.backArrow) {
            if (this.oldPage >= -1) {
               this.page = this.oldPage;
            }
         } else if (button == this.indexArrow) {
            this.openPage(this.book.findPageNumber("index.page1"));
         }

         this.oldPage = -2;
         this.buildPages();
      }
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      super.keyTyped(typedChar, keyCode);
      switch(keyCode) {
      case 30:
      case 203:
         this.actionPerformed(this.previousArrow);
         break;
      case 32:
      case 205:
         this.actionPerformed(this.nextArrow);
         break;
      case 61:
         debug = !debug;
      }

   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      int scroll = Mouse.getDWheel();
      if (scroll < 0) {
         this.actionPerformed(this.nextArrow);
      } else if (scroll > 0) {
         this.actionPerformed(this.previousArrow);
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      boolean right = false;
      mouseX = this.getMouseX(false);
      mouseY = this.getMouseY();
      if ((float)mouseX > (float)PAGE_WIDTH + (float)(PAGE_MARGIN + PAGE_PADDING_LEFT) / PAGE_SCALE) {
         mouseX = this.getMouseX(true);
         right = true;
      }

      int oldPage = this.page;

      for(BookElement element : ImmutableList.copyOf(right ? this.rightElements : this.leftElements)) {
         element.mouseClicked(mouseX, mouseY, mouseButton);
         if (this.page != oldPage) {
            break;
         }
      }

   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      boolean right = false;
      mouseX = this.getMouseX(false);
      mouseY = this.getMouseY();
      if ((float)mouseX > (float)PAGE_WIDTH + (float)(PAGE_MARGIN + PAGE_PADDING_LEFT) / PAGE_SCALE) {
         mouseX = this.getMouseX(true);
         right = true;
      }

      int i = 0;

      while(true) {
         if (right) {
            if (i >= this.rightElements.size()) {
               break;
            }
         } else if (i >= this.leftElements.size()) {
            break;
         }

         BookElement element = right ? (BookElement)this.rightElements.get(i) : (BookElement)this.leftElements.get(i);
         element.mouseReleased(mouseX, mouseY, state);
         ++i;
      }

   }

   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      boolean right = false;
      mouseX = this.getMouseX(false);
      mouseY = this.getMouseY();
      if ((float)mouseX > (float)PAGE_WIDTH + (float)(PAGE_MARGIN + PAGE_PADDING_LEFT) / PAGE_SCALE) {
         mouseX = this.getMouseX(true);
         right = true;
      }

      int i = 0;

      while(true) {
         if (right) {
            if (i >= this.rightElements.size()) {
               break;
            }
         } else if (i >= this.leftElements.size()) {
            break;
         }

         BookElement element = right ? (BookElement)this.rightElements.get(i) : (BookElement)this.leftElements.get(i);
         element.mouseClickMove(mouseX, mouseY, clickedMouseButton);
         ++i;
      }

   }

   public void onGuiClosed() {
      if (this.mc.player != null) {
         PageData page = this.page == 0 ? this.book.findPage(0, this.advancementCache) : this.book.findPage((this.page - 1) * 2 + 1, this.advancementCache);
         if (page == null) {
            page = this.book.findPage((this.page - 1) * 2 + 2, this.advancementCache);
         }

         if (this.page == -1) {
            BookLoader.updateSavedPage(this.mc.player, this.item, "");
         } else if (page != null && page.parent != null) {
            BookLoader.updateSavedPage(this.mc.player, this.item, page.parent.name + "." + page.name);
         }

      }
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   static {
      init();
   }

   public class AdvancementCache implements IListener {
      private HashMap<Advancement, AdvancementProgress> progress = new HashMap();
      private HashMap<ResourceLocation, Advancement> nameCache = new HashMap();

      public AdvancementCache() {
      }

      public AdvancementProgress getProgress(String id) {
         return this.getProgress(this.getAdvancement(id));
      }

      public AdvancementProgress getProgress(Advancement advancement) {
         return (AdvancementProgress)this.progress.get(advancement);
      }

      public Advancement getAdvancement(String id) {
         return (Advancement)this.nameCache.get(new ResourceLocation(id));
      }

      public void onUpdateAdvancementProgress(Advancement advancement, AdvancementProgress advancementProgress) {
         this.progress.put(advancement, advancementProgress);
      }

      public void setSelectedTab(@Nullable Advancement advancement) {
      }

      public void rootAdvancementAdded(Advancement advancement) {
         this.nameCache.put(advancement.getId(), advancement);
      }

      public void rootAdvancementRemoved(Advancement advancement) {
         this.progress.remove(advancement);
         this.nameCache.remove(advancement.getId());
      }

      public void nonRootAdvancementAdded(Advancement advancement) {
         this.nameCache.put(advancement.getId(), advancement);
      }

      public void nonRootAdvancementRemoved(Advancement advancement) {
         this.progress.remove(advancement);
         this.nameCache.remove(advancement.getId());
      }

      public void advancementsCleared() {
         this.progress.clear();
         this.nameCache.clear();
      }
   }
}
