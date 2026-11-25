package org.orecruncher.lib.gui;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiUtils {
   private GuiUtils() {
   }

   public static void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
      fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color);
   }

   public static void drawString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
      fontRendererIn.drawStringWithShadow(text, (float)x, (float)y, color);
   }

   public static void drawRect(int left, int top, int right, int bottom, int color) {
      if (left < right) {
         int i = left;
         left = right;
         right = i;
      }

      if (top < bottom) {
         int j = top;
         top = bottom;
         bottom = j;
      }

      float f3 = (float)(color >> 24 & 255) / 255.0F;
      float f = (float)(color >> 16 & 255) / 255.0F;
      float f1 = (float)(color >> 8 & 255) / 255.0F;
      float f2 = (float)(color & 255) / 255.0F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer vertexbuffer = tessellator.getBuffer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.color(f, f1, f2, f3);
      vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
      vertexbuffer.pos((double)left, (double)bottom, 0.0D).endVertex();
      vertexbuffer.pos((double)right, (double)bottom, 0.0D).endVertex();
      vertexbuffer.pos((double)right, (double)top, 0.0D).endVertex();
      vertexbuffer.pos((double)left, (double)top, 0.0D).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      float zLevel = 0.0F;
      float f = (float)(startColor >> 24 & 255) / 255.0F;
      float f1 = (float)(startColor >> 16 & 255) / 255.0F;
      float f2 = (float)(startColor >> 8 & 255) / 255.0F;
      float f3 = (float)(startColor & 255) / 255.0F;
      float f4 = (float)(endColor >> 24 & 255) / 255.0F;
      float f5 = (float)(endColor >> 16 & 255) / 255.0F;
      float f6 = (float)(endColor >> 8 & 255) / 255.0F;
      float f7 = (float)(endColor & 255) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer t = tessellator.getBuffer();
      t.begin(7, DefaultVertexFormats.POSITION_COLOR);
      t.pos((double)(left + right), (double)top, 0.0D).color(f1, f2, f3, f).endVertex();
      t.pos((double)left, (double)top, 0.0D).color(f1, f2, f3, f).endVertex();
      t.pos((double)left, (double)(top + bottom), 0.0D).color(f5, f6, f7, f4).endVertex();
      t.pos((double)(left + right), (double)(top + bottom), 0.0D).color(f5, f6, f7, f4).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public static void drawTooltipBox(int x, int y, int w, int h, int bg, int grad1, int grad2) {
      drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
      drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
      drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
      drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
      drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
      drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);
      drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
      drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
   }

   public static void drawTexturedModalRect(@Nonnull ResourceLocation texture, int x, int y, int width, int height, @Nonnull Vec2f u, @Nonnull Vec2f v) {
      float zLevel = 0.0F;
      Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer vertexbuffer = tessellator.getBuffer();
      vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      vertexbuffer.pos((double)(x + 0), (double)(y + height), 0.0D).tex((double)u.x, (double)v.x).endVertex();
      vertexbuffer.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)u.y, (double)v.x).endVertex();
      vertexbuffer.pos((double)(x + width), (double)(y + 0), 0.0D).tex((double)u.y, (double)v.y).endVertex();
      vertexbuffer.pos((double)(x + 0), (double)(y + 0), 0.0D).tex((double)u.x, (double)v.y).endVertex();
      tessellator.draw();
   }

   public static Vec2f calculateSpan(int sheetDimension, int first, int second) {
      return new Vec2f((float)first / (float)sheetDimension, (float)second / (float)sheetDimension);
   }
}
