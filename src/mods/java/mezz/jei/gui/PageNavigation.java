package mezz.jei.gui;

import com.arlenh7.eaglercraft.awt.Color;
import com.arlenh7.eaglercraft.awt.Rectangle;
import mezz.jei.Internal;
import mezz.jei.gui.elements.GuiIconButton;
import mezz.jei.gui.elements.IMouseClickedButtonCallback;
import mezz.jei.input.IPaged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class PageNavigation {
   private final IPaged paged;
   private final GuiButton nextButton;
   private final GuiButton backButton;
   private final boolean hideOnSinglePage;
   private String pageNumDisplayString = "1/1";
   private int pageNumDisplayX;
   private int pageNumDisplayY;

   public PageNavigation(IPaged paged, boolean hideOnSinglePage) {
      this.paged = paged;
      GuiHelper guiHelper = Internal.getHelpers().getGuiHelper();
      this.nextButton = new GuiIconButton(0, guiHelper.getArrowNext(), (mc, mouseX, mouseY) -> {
         return paged.nextPage();
      });
      this.backButton = new GuiIconButton(1, guiHelper.getArrowPrevious(), (mc, mouseX, mouseY) -> {
         return paged.previousPage();
      });
      this.hideOnSinglePage = hideOnSinglePage;
   }

   public void updateBounds(Rectangle area) {
      int buttonSize = area.height;
      this.nextButton.x = area.x + area.width - buttonSize;
      this.nextButton.y = area.y;
      this.nextButton.width = this.nextButton.height = buttonSize;
      this.backButton.x = area.x;
      this.backButton.y = area.y;
      this.backButton.width = this.backButton.height = buttonSize;
   }

   public void updatePageState() {
      int pageNum = this.paged.getPageNumber();
      int pageCount = this.paged.getPageCount();
      FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
      this.pageNumDisplayString = pageNum + 1 + "/" + pageCount;
      int pageDisplayWidth = fontRenderer.getStringWidth(this.pageNumDisplayString);
      this.pageNumDisplayX = (this.backButton.x + this.backButton.width + this.nextButton.x) / 2 - pageDisplayWidth / 2;
      this.pageNumDisplayY = this.backButton.y + Math.round((float)(this.backButton.height - fontRenderer.FONT_HEIGHT) / 2.0F);
   }

   public void draw(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
      if (!this.hideOnSinglePage || this.paged.hasNext() || this.paged.hasPrevious()) {
         minecraft.fontRenderer.drawString(this.pageNumDisplayString, (float)this.pageNumDisplayX, (float)this.pageNumDisplayY, Color.white.getRGB(), true);
         this.nextButton.drawButton(minecraft, mouseX, mouseY, partialTicks);
         this.backButton.drawButton(minecraft, mouseX, mouseY, partialTicks);
      }

   }

   public boolean isMouseOver() {
      return this.nextButton.isMouseOver() || this.backButton.isMouseOver();
   }

   public boolean handleMouseClickedButtons(int mouseX, int mouseY) {
      Minecraft minecraft = Minecraft.getMinecraft();
      return this.nextButton.mousePressed(minecraft, mouseX, mouseY) || this.backButton.mousePressed(minecraft, mouseX, mouseY);
   }
}
