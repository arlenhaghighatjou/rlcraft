package rustic.common.book.pages;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.gui.book.GuiBook;
import rustic.common.book.BookCategory;
import rustic.common.book.BookEntryCategory;
import rustic.common.book.BookManager;
import rustic.common.book.BookPage;

public class BookPageCategories extends BookPage {
   public BookPageCategories() {
      super(BookManager.categories_entry);
   }

   public void drawScreen(GuiBook gui, int mouseX, int mouseY, float partialTicks) {
      int y = gui.guiTop + 12;
      int x = gui.guiLeft + 73;
      gui.drawCenteredText(TextFormatting.UNDERLINE + "" + TextFormatting.BOLD + I18n.translateToLocal(BookManager.categories_entry.getName()), x, y, 0);
   }

   public void onOpened(GuiBook gui) {
      int y = gui.guiTop + 28;
      int x = gui.guiLeft + 73 - 16;

      for(BookCategory category : BookManager.categories) {
         gui.getButtonList().add(new BookPageCategories.CategoryButton(gui.nextButtonID(), x, y, 32, gui.getFontRenderer(), category));
         y += 48;
      }

   }

   public void onClosed(GuiBook gui) {
      super.onClosed(gui);
   }

   public void actionPerformed(GuiBook gui, GuiButton button) {
      if (button instanceof BookPageCategories.CategoryButton) {
         gui.goToEntry(((BookPageCategories.CategoryButton)button).getCategoryEntry());
      }

   }

   @SideOnly(Side.CLIENT)
   public class CategoryButton extends GuiButton {
      private int ticksHovered = 0;
      private int frame = 0;
      private FontRenderer fontRenderer;
      private BookCategory category;

      public CategoryButton(int buttonId, int x, int y, int width, FontRenderer fontRenderer, BookCategory category) {
         super(buttonId, x, y, width, width, category.getName());
         this.fontRenderer = fontRenderer;
         this.category = category;
      }

      public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
         if (this.visible) {
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            if (flag) {
               ++this.ticksHovered;
               if (this.ticksHovered % 2 == 0 && this.frame < 7) {
                  ++this.frame;
               }

               String text = I18n.translateToLocal(this.displayString);
               int stringWidth = this.fontRenderer.getStringWidth(text);
               this.fontRenderer.drawString(text, this.x + this.width / 2 - stringWidth / 2, this.y + this.height, 0);
               GlStateManager.color(0.171875F, 0.4453125F, 0.171875F, 1.0F);
            } else {
               this.ticksHovered = 0;
               if (this.frame > 0) {
                  --this.frame;
               }

               GlStateManager.color(0.0F, 0.0F, 0.0F, 1.0F);
            }

            mc.getTextureManager().bindTexture(this.category.getIcon());
            Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, 0.0F, (float)this.frame * 32.0F, 32, 32, 32.0F, 256.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
         }

      }

      public BookEntryCategory getCategoryEntry() {
         return this.category.getCategoryEntry();
      }
   }
}
