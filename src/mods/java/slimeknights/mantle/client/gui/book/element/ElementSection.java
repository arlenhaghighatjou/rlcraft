package slimeknights.mantle.client.gui.book.element;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import slimeknights.mantle.client.book.data.SectionData;

public class ElementSection extends SizedBookElement {
   public static final int IMG_SIZE = 32;
   public static final int WIDTH = 42;
   public static final int HEIGHT = 42;
   private SectionData section;

   public ElementSection(int x, int y, SectionData section) {
      super(x, y, 42, 42);
      this.section = section;
   }

   public void draw(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
      boolean unlocked = this.section.isUnlocked(this.parent.advancementCache);
      boolean hover = this.isHovered(mouseX, mouseY);
      if (this.section.icon != null) {
         int iconX = this.x + 21 - 16;
         int iconY = this.y + 21 - 16;
         if (hover) {
            drawRect(iconX, iconY, iconX + 32, iconY + 32, this.parent.book.appearance.hoverColor);
         }

         if (unlocked) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, hover ? 1.0F : 0.5F);
         } else {
            float r = (float)(this.parent.book.appearance.lockedSectionColor >> 16 & 255) / 255.0F;
            float g = (float)(this.parent.book.appearance.lockedSectionColor >> 8 & 255) / 255.0F;
            float b = (float)(this.parent.book.appearance.lockedSectionColor & 255) / 255.0F;
            GlStateManager.color(r, g, b, 0.75F);
         }

         if (this.section.icon.item == null) {
            if (this.section.icon.location != null) {
               this.renderEngine.bindTexture(this.section.icon.location);
               drawScaledCustomSizeModalRect(iconX, iconY, (float)this.section.icon.u, (float)this.section.icon.v, this.section.icon.uw, this.section.icon.vh, 32, 32, (float)this.section.icon.texWidth, (float)this.section.icon.texHeight);
            }
         } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)iconX, (float)iconY, 0.0F);
            GlStateManager.scale(2.0F, 2.0F, 1.0F);
            RenderHelper.enableGUIStandardItemLighting();
            this.mc.getRenderItem().renderItemAndEffectIntoGUI((ItemStack)this.section.icon.item.getItems().get(0), 0, 0);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
         }
      }

      if (this.section.parent.appearance.drawSectionListText) {
         int textW = fontRenderer.getStringWidth(this.section.getTitle());
         int textX = this.x + 21 - textW / 2;
         int textY = this.y + 42 - fontRenderer.FONT_HEIGHT / 2;
         fontRenderer.drawString(this.section.getTitle(), textX, textY, hover ? -16777216 : 2130706432);
      }

   }

   public void drawOverlay(int mouseX, int mouseY, float partialTicks, FontRenderer fontRenderer) {
      if (this.section != null && this.isHovered(mouseX, mouseY)) {
         List<String> text = new ArrayList();
         text.add(this.section.getTitle());
         if (!this.section.isUnlocked(this.parent.advancementCache)) {
            text.add(TextFormatting.RED + "Locked");
            text.add("Requirements:");
            text.addAll(this.section.requirements);
         }

         this.drawHoveringText(text, mouseX, mouseY, fontRenderer);
      }

   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0 && this.section != null && this.section.isUnlocked(this.parent.advancementCache) && this.isHovered(mouseX, mouseY)) {
         this.parent.openPage(this.parent.book.getFirstPageNumber(this.section, this.parent.advancementCache));
      }

   }
}
