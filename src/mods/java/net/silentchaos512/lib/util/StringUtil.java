package net.silentchaos512.lib.util;

import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StringUtil {
   public StringUtil() {
   }

   @SideOnly(Side.CLIENT)
   public static void drawSplitString(FontRenderer renderer, String strg, int x, int y, int width, int color, boolean shadow) {
      List list = renderer.listFormattedStringToWidth(strg, width);

      for(int i = 0; i < list.size(); ++i) {
         String s1 = (String)list.get(i);
         renderer.drawString(s1, (float)x, (float)(y + i * renderer.FONT_HEIGHT), color, shadow);
      }

   }

   @SideOnly(Side.CLIENT)
   public static void renderScaledAsciiString(FontRenderer font, String text, int x, int y, int color, boolean shadow, float scale) {
      GlStateManager.pushMatrix();
      GlStateManager.scale(scale, scale, scale);
      boolean oldUnicode = font.getUnicodeFlag();
      font.setUnicodeFlag(false);
      font.drawString(text, (float)x / scale, (float)y / scale, color, shadow);
      font.setUnicodeFlag(oldUnicode);
      GlStateManager.popMatrix();
   }

   @SideOnly(Side.CLIENT)
   public static void renderSplitScaledAsciiString(FontRenderer font, String text, int x, int y, int color, boolean shadow, float scale, int length) {
      List<String> lines = font.listFormattedStringToWidth(text, (int)((float)length / scale));

      for(int i = 0; i < lines.size(); ++i) {
         renderScaledAsciiString(font, (String)lines.get(i), x, y + i * (int)((float)font.FONT_HEIGHT * scale + 3.0F), color, shadow, scale);
      }

   }
}
