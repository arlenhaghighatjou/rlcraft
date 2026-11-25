package com.lycanitesmobs.client.gui;

import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class BaseScreen extends GuiScreen {
   public BaseScreen() {
   }

   public FontRenderer getFontRenderer() {
      return LycanitesMobs.proxy.getFontRenderer();
   }

   public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor, boolean shadow) {
      if (shadow) {
         GlStateManager.translate(0.5F, 0.5F, 0.0F);
         this.getFontRenderer().drawSplitString(str, x, y, wrapWidth, 4473924);
         GlStateManager.translate(-0.5F, -0.5F, 0.0F);
      }

      this.getFontRenderer().drawSplitString(str, x, y, wrapWidth, textColor);
   }

   public void drawTexture(ResourceLocation texture, float x, float y, float z, float u, float v, float width, float height) {
      GlStateManager.enableBlend();
      GlStateManager.disableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableAlpha();
      this.mc.getTextureManager().bindTexture(texture);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      buffer.pos((double)x, (double)(y + height), (double)z).tex(0.0D, (double)v).endVertex();
      buffer.pos((double)(x + width), (double)(y + height), (double)z).tex((double)u, (double)v).endVertex();
      buffer.pos((double)(x + width), (double)y, (double)z).tex((double)u, 0.0D).endVertex();
      buffer.pos((double)x, (double)y, (double)z).tex(0.0D, 0.0D).endVertex();
      tessellator.draw();
      GlStateManager.disableBlend();
      GlStateManager.enableDepth();
      GlStateManager.depthMask(true);
      GlStateManager.enableAlpha();
   }

   public void drawTexturedTiled(ResourceLocation texture, float x, float y, float z, float u, float v, float width, float height, float resolution) {
      GlStateManager.enableBlend();
      GlStateManager.disableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableAlpha();
      this.mc.getTextureManager().bindTexture(texture);
      float scaleX = 0.00390625F * resolution;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      buffer.pos((double)(x + 0.0F), (double)(y + height), (double)z).tex((double)((u + 0.0F) * scaleX), (double)((v + height) * scaleX)).endVertex();
      buffer.pos((double)(x + width), (double)(y + height), (double)z).tex((double)((u + width) * scaleX), (double)((v + height) * scaleX)).endVertex();
      buffer.pos((double)(x + width), (double)(y + 0.0F), (double)z).tex((double)((u + width) * scaleX), (double)((v + 0.0F) * scaleX)).endVertex();
      buffer.pos((double)(x + 0.0F), (double)(y + 0.0F), (double)z).tex((double)((u + 0.0F) * scaleX), (double)((v + 0.0F) * scaleX)).endVertex();
      tessellator.draw();
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

   /** @deprecated */
   @Deprecated
   public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
      this.drawTexturedModalRect(x, y, u, v, width, height, 1);
   }

   /** @deprecated */
   @Deprecated
   public void drawTexturedModalRect(int x, int y, int u, int v, int width, int height, int resolution) {
      float scaleX = 0.00390625F * (float)resolution;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer vertexbuffer = tessellator.getBuffer();
      vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      vertexbuffer.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(u + 0) * scaleX), (double)((float)(v + height) * scaleX)).endVertex();
      vertexbuffer.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(u + width) * scaleX), (double)((float)(v + height) * scaleX)).endVertex();
      vertexbuffer.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(u + width) * scaleX), (double)((float)(v + 0) * scaleX)).endVertex();
      vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(u + 0) * scaleX), (double)((float)(v + 0) * scaleX)).endVertex();
      tessellator.draw();
   }
}
