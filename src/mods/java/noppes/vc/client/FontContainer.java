package noppes.vc.client;

import net.minecraft.client.Minecraft;

public class FontContainer {
   public boolean useCustomFont = false;

   public FontContainer() {
   }

   public FontContainer(String fontType, int fontSize) {
   }

   public int height(String text) {
      return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
   }

   public int width(String text) {
      return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
   }

   public FontContainer copy() {
      FontContainer font = new FontContainer();
      font.useCustomFont = this.useCustomFont;
      return font;
   }

   public void drawString(String text, int x, int y, int color) {
      Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, (float)x, (float)y, color);
   }

   public String getName() {
      return "Minecraft";
   }

   public void clear() {
   }
}
