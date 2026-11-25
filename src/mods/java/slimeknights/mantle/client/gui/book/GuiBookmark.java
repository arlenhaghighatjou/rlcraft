package slimeknights.mantle.client.gui.book;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.book.data.BookmarkData;

public class GuiBookmark extends GuiButton {
   private static final ResourceLocation TEX_BOOK = new ResourceLocation("mantle:textures/gui/book.png");
   public static final int WIDTH = 31;
   public static final int HEIGHT = 9;
   public static final int TEX_X = 0;
   public static final int TEX_Y = 400;
   public static final int ADD_W = 5;
   public static final int ADD_H = 5;
   public static final int ADD_X = 32;
   public static final int ADD_Y = 402;
   public static final float TEXT_SCALE = 0.5F;
   public int type = 0;
   public final BookmarkData data;

   public GuiBookmark(BookmarkData data) {
      super(-1337, -500, -500, 31, 9, "");
      this.data = data;
   }

   public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         int tex_y = 400 + 9 * this.type;
         mc.renderEngine.bindTexture(TEX_BOOK);
         float r = (float)(this.data.color >> 16 & 255) / 255.0F;
         float g = (float)(this.data.color >> 8 & 255) / 255.0F;
         float b = (float)(this.data.color & 255) / 255.0F;
         GlStateManager.color(r, g, b);
         Gui.drawScaledCustomSizeModalRect(this.x, this.y, 0.0F, (float)tex_y, 31, 9, this.width, this.height, 512.0F, 512.0F);
         if (this.data.text != null && !this.data.text.isEmpty()) {
            TextDataRenderer.drawScaledString(mc.fontRenderer, this.data.text, (float)(this.x + 1), (float)(this.y + this.height / 2) - (float)mc.fontRenderer.FONT_HEIGHT * 0.5F / 2.0F + 1.0F, -1, true, 0.5F);
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F);
         if (this.data.page.equals("ADD")) {
            Gui.drawModalRectWithCustomSizedTexture(this.x + this.width / 2 - 2, this.y + this.height / 2 - 2, 32.0F, 402.0F, 5, 5, 512.0F, 512.0F);
         }
      }

   }
}
