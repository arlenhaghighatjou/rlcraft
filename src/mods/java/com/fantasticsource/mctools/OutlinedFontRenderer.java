package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.Compat;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class OutlinedFontRenderer {
   public static final int LINE_HEIGHT;
   private static final FontRenderer FR;

   public OutlinedFontRenderer() {
   }

   public static void draw(String text, float x, float y, Color color, Color shadowColor) {
      if (shadowColor.a() > 0 || shadowColor.a() > 0) {
         ++x;
         ++y;
         int c = shadowColor.color() >>> 8 | (shadowColor.color() & 255) << 24;
         if ((c & -67108864) != 0) {
            FR.drawString(text, x - 1.0F, y, c, false);
            FR.drawString(text, x + 1.0F, y, c, false);
            FR.drawString(text, x, y - 1.0F, c, false);
            FR.drawString(text, x, y + 1.0F, c, false);
            if (Compat.smoothfont) {
               FR.drawString(text, x + 0.5F, y + 0.5F, c, false);
               FR.drawString(text, x + 0.5F, y - 0.5F, c, false);
               FR.drawString(text, x - 0.5F, y + 0.5F, c, false);
               FR.drawString(text, x - 0.5F, y - 0.5F, c, false);
            }
         }

         c = color.color() >>> 8 | (color.color() & 255) << 24;
         if ((c & -67108864) != 0) {
            FR.drawString(text, x, y, c, false);
         }

      }
   }

   public static int getStringWidth(String text) {
      return FR.getStringWidth(text) + 1;
   }

   static {
      LINE_HEIGHT = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 1;
      FR = Minecraft.getMinecraft().fontRenderer;
   }
}
