package com.lycanitesmobs.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class DrawHelper {
   protected Minecraft minecraft;
   protected FontRenderer fontRenderer;

   public DrawHelper(Minecraft minecraft, FontRenderer fontRenderer) {
      this.minecraft = minecraft;
      this.fontRenderer = fontRenderer;
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public FontRenderer getFontRenderer() {
      if (this.fontRenderer == null) {
         this.fontRenderer = this.minecraft.fontRenderer;
      }

      return this.fontRenderer;
   }

   public int getStringWidth(String text) {
      return this.getFontRenderer().getStringWidth(text);
   }

   public int getWordWrappedHeight(String text, int wrapWidth) {
      return this.getFontRenderer().getWordWrappedHeight(text, wrapWidth);
   }

   public void drawTexture(ResourceLocation texture, float x, float y, float z, float u, float v, float width, float height) {
      this.preDraw();
      this.getMinecraft().getTextureManager().bindTexture(texture);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      buffer.pos((double)x, (double)(y + height), (double)z).tex(0.0D, (double)v).endVertex();
      buffer.pos((double)(x + width), (double)(y + height), (double)z).tex((double)u, (double)v).endVertex();
      buffer.pos((double)(x + width), (double)y, (double)z).tex((double)u, 0.0D).endVertex();
      buffer.pos((double)x, (double)y, (double)z).tex(0.0D, 0.0D).endVertex();
      tessellator.draw();
      this.postDraw();
   }

   public void drawTextureTiled(ResourceLocation texture, float x, float y, float z, float u, float v, float width, float height, float resolution) {
      this.preDraw();
      this.getMinecraft().getTextureManager().bindTexture(texture);
      float scaleX = 0.00390625F * resolution;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      buffer.pos((double)(x + 0.0F), (double)(y + height), (double)z).tex((double)((u + 0.0F) * scaleX), (double)((v + height) * scaleX)).endVertex();
      buffer.pos((double)(x + width), (double)(y + height), (double)z).tex((double)((u + width) * scaleX), (double)((v + height) * scaleX)).endVertex();
      buffer.pos((double)(x + width), (double)(y + 0.0F), (double)z).tex((double)((u + width) * scaleX), (double)((v + 0.0F) * scaleX)).endVertex();
      buffer.pos((double)(x + 0.0F), (double)(y + 0.0F), (double)z).tex((double)((u + 0.0F) * scaleX), (double)((v + 0.0F) * scaleX)).endVertex();
      tessellator.draw();
      this.postDraw();
   }

   public void drawBar(ResourceLocation texture, int x, int y, float z, float width, float height, int segments, int segmentLimit) {
      boolean reverse = segmentLimit < 0;
      if (reverse) {
         segmentLimit = -segmentLimit;
      }

      for(int i = 0; i < segments; ++i) {
         int currentSegment = i;
         if (reverse) {
            currentSegment = segmentLimit - i - 1;
         }

         this.drawTexture(texture, (float)x + width * (float)currentSegment, (float)y, z, 1.0F, 1.0F, width, height);
      }

   }

   public void preDraw() {
      GlStateManager.enableBlend();
      GlStateManager.disableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableAlpha();
   }

   public void postDraw() {
      GlStateManager.disableBlend();
      GlStateManager.enableDepth();
      GlStateManager.depthMask(true);
      GlStateManager.enableAlpha();
   }

   public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
      this.drawTexturedModalRect(x, y, u, v, width, height, 1);
   }

   public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int resolution) {
      this.preDraw();
      float scaleX = 0.00390625F * (float)resolution;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      buffer.pos((double)((float)x), (double)((float)(y + height)), 0.0D).tex((double)((float)u * scaleX), (double)((float)(v + height) * scaleX)).endVertex();
      buffer.pos((double)((float)(x + width)), (double)((float)(y + height)), 0.0D).tex((double)((float)(u + width) * scaleX), (double)((float)(v + height) * scaleX)).endVertex();
      buffer.pos((double)((float)(x + width)), (double)((float)y), 0.0D).tex((double)((float)(u + width) * scaleX), (double)((float)v * scaleX)).endVertex();
      buffer.pos((double)((float)x), (double)((float)y), 0.0D).tex((double)((float)u * scaleX), (double)((float)v * scaleX)).endVertex();
      tessellator.draw();
      this.postDraw();
   }
}
