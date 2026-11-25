package rustic.common.book.pages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.gui.book.GuiBook;
import rustic.common.book.BookEntry;
import rustic.common.book.BookPage;

public class BookPageText extends BookPage {
   protected List<String> textKeys = new ArrayList();
   protected Set<BookEntry> relatedEntries = new HashSet();

   public BookPageText(BookEntry entry, String... textKeys) {
      super(entry);

      for(String textKey : textKeys) {
         this.textKeys.add("book.rustic.text." + textKey);
      }

   }

   public void drawScreen(GuiBook gui, int mouseX, int mouseY, float partialTicks) {
      int y = gui.guiTop + 12;
      int x = gui.guiLeft + 73;
      int maxY = gui.guiTop + 180 - 19;
      gui.drawCenteredText(TextFormatting.UNDERLINE + "" + TextFormatting.BOLD + I18n.translateToLocal(this.entry.getName()), x, y, 0);
      y = y + gui.getFontRenderer().FONT_HEIGHT + 4;
      x = gui.guiLeft + 16;
      float textScale = 0.75F;
      GlStateManager.pushMatrix();
      GlStateManager.scale(textScale, textScale, textScale);

      for(int i = 0; i < this.textKeys.size(); ++i) {
         String text = I18n.translateToLocal((String)this.textKeys.get(i));
         int increment = (int)((float)(gui.getFontRenderer().FONT_HEIGHT + 2) * textScale);

         for(String line : gui.getFontRenderer().listFormattedStringToWidth(text, (int)(114.0F / textScale))) {
            if (y + increment > maxY) {
               break;
            }

            gui.getFontRenderer().drawString(line, (int)((float)x / textScale), (int)((float)y / textScale), 0);
            y += increment;
         }

         y += (int)(4.0F * textScale);
      }

      GlStateManager.popMatrix();
      if (this.relatedEntries.size() > 0) {
         String text = TextFormatting.UNDERLINE + I18n.translateToLocal("book.rustic.label.related_entries");
         int textWidth = gui.getFontRenderer().getStringWidth(text);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         gui.mc.getTextureManager().bindTexture(GuiBook.BOOK_BACKGROUND);
         gui.drawTexturedModalRect(gui.guiLeft + 146, gui.guiTop + 10, 136 - (textWidth + 2), 210, textWidth + 2 + 10, 16);
         gui.getFontRenderer().drawString(text, gui.guiLeft + 146 + 2, gui.guiTop + 10 + 4, 0);
      }

   }

   public void onOpened(GuiBook gui) {
      super.onOpened(gui);
      gui.addNavButtons();
      int x = gui.guiLeft + 146;
      int y = gui.guiTop + 10 + 16 + 2;
      int i = 0;

      for(BookEntry entry : this.relatedEntries) {
         if (i < 9) {
            gui.getButtonList().add(new BookPageText.RelatedEntryButton(gui.nextButtonID(), x, y, gui.getFontRenderer(), entry));
            y += 16;
            ++i;
         }
      }

   }

   public void onClosed(GuiBook gui) {
      super.onClosed(gui);
   }

   public void actionPerformed(GuiBook gui, GuiButton button) {
      if (button instanceof BookPageText.RelatedEntryButton) {
         gui.goToEntry(((BookPageText.RelatedEntryButton)button).getEntry());
      }

   }

   public BookPageText addRelatedEntry(BookEntry entry) {
      if (entry != null) {
         this.relatedEntries.add(entry);
      }

      return this;
   }

   public BookPageText addRelatedEntries(BookEntry... entries) {
      for(BookEntry entry : entries) {
         if (entry != null) {
            this.relatedEntries.add(entry);
         }
      }

      return this;
   }

   @SideOnly(Side.CLIENT)
   public class RelatedEntryButton extends GuiButton {
      private static final int BASE_WIDTH = 24;
      private static final int BASE_HEIGHT = 16;
      private int ticksHovered = 0;
      private FontRenderer fontRenderer;
      private final BookEntry entry;

      public RelatedEntryButton(int buttonId, int x, int y, FontRenderer fontRenderer, BookEntry entry) {
         super(buttonId, x, y, 24, 16, entry.getName());
         this.fontRenderer = fontRenderer;
         this.entry = entry;
      }

      public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
         if (this.visible) {
            String text = I18n.translateToLocal(this.displayString);
            int textWidth = this.fontRenderer.getStringWidth(text);
            boolean flag = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            if (flag) {
               if (this.width < 140 && this.width < textWidth + 24 + 2) {
                  ++this.ticksHovered;
                  this.width = Math.min(24 + this.ticksHovered * 8, Math.min(textWidth + 24 + 2, 144));
               }
            } else {
               this.ticksHovered = 0;
               if (this.width > 24) {
                  this.width = Math.max(this.width - 12, 24);
               }
            }

            mc.getTextureManager().bindTexture(GuiBook.BOOK_BACKGROUND);
            Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, (float)(146 - this.width), 226.0F, this.width, this.height, 256.0F, 256.0F);
            text = this.fontRenderer.trimStringToWidth(text, this.width - 24, true);
            textWidth = this.fontRenderer.getStringWidth(text);
            this.fontRenderer.drawString(text, this.x + (this.width - 24) - textWidth, this.y + 4, 0);
            if (this.entry != null && !this.entry.getIcon().isEmpty()) {
               GlStateManager.pushMatrix();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               RenderHelper.enableGUIStandardItemLighting();
               mc.getRenderItem().renderItemIntoGUI(this.entry.getIcon(), this.x + this.width - 22, this.y);
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
