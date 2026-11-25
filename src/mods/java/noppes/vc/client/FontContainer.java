package noppes.vc.client;

import java.awt.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import noppes.vc.VariedCommodities;
import noppes.vc.shared.util.TrueTypeFont;

public class FontContainer {
   private TrueTypeFont textFont = null;
   public boolean useCustomFont = true;

   public FontContainer() {
   }

   public FontContainer(String fontType, int fontSize) {
      this.textFont = new TrueTypeFont(new Font(fontType, 0, fontSize), 1.0F);
      this.useCustomFont = !fontType.equalsIgnoreCase("minecraft");

      try {
         if (!this.useCustomFont || fontType.isEmpty() || fontType.equalsIgnoreCase("default")) {
            this.textFont = new TrueTypeFont(new ResourceLocation("variedcommodities", "opensans.ttf"), fontSize, 1.0F);
         }
      } catch (Exception var4) {
         VariedCommodities.Log.info("Failed loading font so using Arial");
      }

   }

   public int height(String text) {
      return this.useCustomFont ? this.textFont.height(text) : Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
   }

   public int width(String text) {
      return this.useCustomFont ? this.textFont.width(text) : Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
   }

   public FontContainer copy() {
      FontContainer font = new FontContainer();
      font.textFont = this.textFont;
      font.useCustomFont = this.useCustomFont;
      return font;
   }

   public void drawString(String text, int x, int y, int color) {
      if (this.useCustomFont) {
         this.textFont.draw(text, (float)x, (float)y, color);
      } else {
         Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, (float)x, (float)y, color);
      }

   }

   public String getName() {
      return !this.useCustomFont ? "Minecraft" : this.textFont.getFontName();
   }

   public void clear() {
      if (this.textFont != null) {
         this.textFont.dispose();
      }

   }
}
