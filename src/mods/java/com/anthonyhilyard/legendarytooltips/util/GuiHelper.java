package com.anthonyhilyard.legendarytooltips.util;

import net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiHelper {
   public GuiHelper() {
   }

   public static void drawGradientRectHorizontal(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
      float startAlpha = (float)(startColor >> 24 & 255) / 255.0F;
      float startRed = (float)(startColor >> 16 & 255) / 255.0F;
      float startGreen = (float)(startColor >> 8 & 255) / 255.0F;
      float startBlue = (float)(startColor & 255) / 255.0F;
      float endAlpha = (float)(endColor >> 24 & 255) / 255.0F;
      float endRed = (float)(endColor >> 16 & 255) / 255.0F;
      float endGreen = (float)(endColor >> 8 & 255) / 255.0F;
      float endBlue = (float)(endColor & 255) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA, RealOpenGLEnums.GL_ONE, RealOpenGLEnums.GL_ZERO);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      buffer.pos((double)right, (double)top, (double)zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
      buffer.pos((double)left, (double)top, (double)zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
      buffer.pos((double)left, (double)bottom, (double)zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
      buffer.pos((double)right, (double)bottom, (double)zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }
}
