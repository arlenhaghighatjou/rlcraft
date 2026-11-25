package com.bloodnbonesgaming.loadingscreens.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiElementColor extends GuiElementTexture {
   public GuiElementColor(EnumGuiLocation location, ResourceLocation texture, int imageWidth, int imageHeight) {
      super(location, texture, imageWidth, imageHeight);
   }

   public void render(Minecraft minecraft, int guiWidth, int guiHeight) {
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      this.drawTexture(this.location.getX(guiWidth, (int)(this.relRenderWidth * (double)guiWidth)), this.location.getY(guiHeight, (int)(this.relRenderHeight * (double)guiHeight)), this.imageWidth, this.imageHeight, (int)(this.relRenderWidth * (double)guiWidth), (int)(this.relRenderHeight * (double)guiHeight), 0.0D, 0.0D, (double)this.imageWidth, (double)this.imageHeight);
   }

   void drawTexture(int x, int y, int texWidth, int texHeight, int width, int height, double texStartX, double texStartY, double texEndX, double texEndY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos((double)(x + 0), (double)(y + height), 0.0D).color(255, 0, 0, 255).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + height), 0.0D).color(255, 0, 0, 255).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + 0), 0.0D).color(255, 0, 0, 255).endVertex();
      bufferbuilder.pos((double)(x + 0), (double)(y + 0), 0.0D).color(255, 0, 0, 255).endVertex();
      tessellator.draw();
   }
}
