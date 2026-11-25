package com.bloodnbonesgaming.loadingscreens.client.gui;

import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiElementTextureStretch extends GuiElementTexture {
   public GuiElementTextureStretch(EnumGuiLocation location, ResourceLocation texture) {
      super(location, texture);
   }

   public GuiElementTextureStretch(EnumGuiLocation location, ResourceLocation texture, int imageWidth, int imageHeight) {
      super(location, texture, imageWidth, imageHeight);
   }

   protected void drawTexture(int x, int y, int texWidth, int texHeight, int width, int height, double texStartX, double texStartY, double texEndX, double texEndY) {
      double xStartPos = texStartX / (double)texWidth;
      double yStartPos = texStartY / (double)texHeight;
      double xEndPos = texEndX / (double)texWidth;
      double yEndPos = texEndY / (double)texHeight;
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)(x + 0), (double)(y + height), 0.0D).tex(xStartPos, yEndPos).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + height), 0.0D).tex(xEndPos, yEndPos).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + 0), 0.0D).tex(xEndPos, yStartPos).endVertex();
      bufferbuilder.pos((double)(x + 0), (double)(y + 0), 0.0D).tex(xStartPos, yStartPos).endVertex();
      tessellator.draw();
   }
}
