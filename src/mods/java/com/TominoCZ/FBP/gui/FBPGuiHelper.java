package com.TominoCZ.FBP.gui;

import com.TominoCZ.FBP.FBP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class FBPGuiHelper extends GuiScreen {
   public static final String on = "\u00a7a ON";
   public static final String off = "\u00a7cOFF";

   public FBPGuiHelper() {
   }

   public static void background(int top, int bottom, int width, int height) {
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer WorldRenderer = tessellator.getBuffer();
      drawContainerBackground(tessellator, top, bottom, 0, width);
      GlStateManager.disableDepth();
      overlayBackground(0, top, 255, 255, top, bottom, 0, width);
      overlayBackground(bottom, height, 255, 255, top, bottom, 0, width);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ZERO, DestFactor.ONE);
      GlStateManager.disableAlpha();
      GlStateManager.shadeModel(7425);
      GlStateManager.disableTexture2D();
      int i1 = 4;
      WorldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      WorldRenderer.pos(0.0D, (double)(top + 4), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 0).endVertex();
      WorldRenderer.pos((double)width, (double)(top + 4), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 0).endVertex();
      WorldRenderer.pos((double)width, (double)top, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
      WorldRenderer.pos(0.0D, (double)top, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
      tessellator.draw();
      WorldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      WorldRenderer.pos(0.0D, (double)bottom, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      WorldRenderer.pos((double)width, (double)bottom, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      WorldRenderer.pos((double)width, (double)(bottom - 4), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 0).endVertex();
      WorldRenderer.pos(0.0D, (double)(bottom - 4), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 0).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.shadeModel(7424);
      GlStateManager.enableAlpha();
      GlStateManager.disableBlend();
   }

   public static void drawRect(double x, double y, double x2, double y2, int red, int green, int blue, int alpha) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer WorldRenderer = tessellator.getBuffer();
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      WorldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      WorldRenderer.pos(x, y + y2, 0.0D).color(red, green, blue, alpha).endVertex();
      WorldRenderer.pos(x + x2, y + y2, 0.0D).color(red, green, blue, alpha).endVertex();
      WorldRenderer.pos(x + x2, y, 0.0D).color(red, green, blue, alpha).endVertex();
      WorldRenderer.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
   }

   public static void drawTitle(int y, int screenWidth, int screenHeight, FontRenderer fr) {
      if (!FBP.isEnabled()) {
         _drawCenteredString(fr, "= disabled =", screenWidth / 2, y - 35, fr.getColorCode('c'));
      }

      _drawCenteredString(fr, "= \u00a7LFancy Block Particles =", screenWidth / 2, y - 27, fr.getColorCode('6'));
      String version = FMLCommonHandler.instance().findContainerFor(FBP.INSTANCE).getVersion();
      _drawCenteredString(fr, "= \u00a7L" + version + " =", screenWidth / 2, y - 17, fr.getColorCode('a'));
   }

   protected static void _drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, int color) {
      fontRendererIn.drawStringWithShadow(text, (float)(x - fontRendererIn.getStringWidth(text) / 2), (float)y, color);
   }

   protected static void overlayBackground(int startY, int endY, int startAlpha, int endAlpha, int top, int bottom, int left, int right) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer WorldRenderer = tessellator.getBuffer();
      Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      WorldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      WorldRenderer.pos((double)left, (double)endY, 0.0D).tex(0.0D, (double)((float)endY / 32.0F)).color(64, 64, 64, endAlpha).endVertex();
      WorldRenderer.pos((double)(left + right), (double)endY, 0.0D).tex((double)((float)right / 32.0F), (double)((float)endY / 32.0F)).color(64, 64, 64, endAlpha).endVertex();
      WorldRenderer.pos((double)(left + right), (double)startY, 0.0D).tex((double)((float)right / 32.0F), (double)((float)startY / 32.0F)).color(64, 64, 64, startAlpha).endVertex();
      WorldRenderer.pos((double)left, (double)startY, 0.0D).tex(0.0D, (double)((float)startY / 32.0F)).color(64, 64, 64, startAlpha).endVertex();
      tessellator.draw();
   }

   protected static void drawContainerBackground(Tessellator tessellator, int top, int bottom, int left, int right) {
      WorldRenderer buffer = tessellator.getBuffer();
      Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      buffer.pos((double)left, (double)bottom, 0.0D).tex((double)((float)left / f), (double)((float)bottom / f)).color(32, 32, 32, 255).endVertex();
      buffer.pos((double)right, (double)bottom, 0.0D).tex((double)((float)right / f), (double)((float)bottom / f)).color(32, 32, 32, 255).endVertex();
      buffer.pos((double)right, (double)top, 0.0D).tex((double)((float)right / f), (double)((float)top / f)).color(32, 32, 32, 255).endVertex();
      buffer.pos((double)left, (double)top, 0.0D).tex((double)((float)left / f), (double)((float)top / f)).color(32, 32, 32, 255).endVertex();
      tessellator.draw();
   }

   public static boolean isMouseInsideCircle(int mouseX, int mouseY, double d, double e, double radius) {
      double X = d - (double)mouseX;
      double Y = e - (double)mouseY;
      return Math.sqrt(X * X + Y * Y) <= radius;
   }
}
