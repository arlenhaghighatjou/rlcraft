package com.fantasticsource.mctools;

import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class MonoASCIIFontRenderer {
   public static final int LINE_HEIGHT;
   public static final int CHAR_WIDTH = 8;
   public static final int CHAR_HEIGHT = 8;
   private static final TextureManager TEXTURE_MANAGER = Minecraft.getMinecraft().getTextureManager();
   private static final double CHAR_UV = 0.0625D;
   private static final double CHAR_U_TO_RIGHT = 0.0625D - 0.0625D / (double)(CHAR_WIDTH << 1);
   private static final double CHAR_V_TO_BOTTOM = 0.0625D - 0.0625D / (double)(CHAR_HEIGHT << 1);
   private static final ResourceLocation TEXTURE = new ResourceLocation("fantasticlib", "image/monospace.png");

   public MonoASCIIFontRenderer() {
   }

   public static void draw(String text, double x, double y, Color color, Color shadowColor) {
      if (shadowColor.a() > 0 || color.a() > 0) {
         GlStateManager.disableLighting();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableTexture2D();
         TEXTURE_MANAGER.bindTexture(TEXTURE);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferBuilder = tessellator.getBuffer();
         bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
         if (shadowColor.a() > 0) {
            drawText(text, bufferBuilder, x, y + 1.0D, shadowColor);
            drawText(text, bufferBuilder, x + 2.0D, y + 1.0D, shadowColor);
            drawText(text, bufferBuilder, x + 1.0D, y, shadowColor);
            drawText(text, bufferBuilder, x + 1.0D, y + 2.0D, shadowColor);
         }

         if (color.a() > 0) {
            drawText(text, bufferBuilder, x + 1.0D, y + 1.0D, color);
         }

         tessellator.draw();
      }
   }

   public static int getStringWidth(String text) {
      return text.length() * (CHAR_WIDTH + 2);
   }

   private static void drawText(String text, BufferBuilder bufferBuilder, double left, double top, Color color) {
      for(char c : text.toCharArray()) {
         if (c < 256) {
            drawChar(c, bufferBuilder, left, top, color.r(), color.g(), color.b(), color.a());
         }

         left += (double)(CHAR_WIDTH + 2);
      }

   }

   private static void drawChar(char c, BufferBuilder bufferBuilder, double left, double top, int r, int g, int b, int a) {
      double u = (double)(c % 16) * 0.0625D;
      double v = (double)(c >>> 4) * 0.0625D;
      bufferBuilder.pos(left, top, 0.0D).tex(u, v).lightmap(15728880, 15728880).color(r, g, b, a).endVertex();
      bufferBuilder.pos(left, top + (double)CHAR_HEIGHT, 0.0D).tex(u, v + CHAR_V_TO_BOTTOM).lightmap(15728880, 15728880).color(r, g, b, a).endVertex();
      bufferBuilder.pos(left + (double)CHAR_WIDTH, top + (double)CHAR_HEIGHT, 0.0D).tex(u + CHAR_U_TO_RIGHT, v + CHAR_V_TO_BOTTOM).lightmap(15728880, 15728880).color(r, g, b, a).endVertex();
      bufferBuilder.pos(left + (double)CHAR_WIDTH, top, 0.0D).tex(u + CHAR_U_TO_RIGHT, v).lightmap(15728880, 15728880).color(r, g, b, a).endVertex();
   }

   static {
      LINE_HEIGHT = CHAR_HEIGHT + 2;
   }
}
