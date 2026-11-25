package com.teamacronymcoders.base.util;

import com.teamacronymcoders.base.client.ClientHelper;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

public class GuiHelper extends GuiScreen {
   private Minecraft mc = Minecraft.getMinecraft();
   private FontRenderer fontRenderer;
   public static final ResourceLocation BLOCK_TEX = TextureMap.LOCATION_BLOCKS_TEXTURE;

   public GuiHelper() {
      this.fontRenderer = this.mc.fontRenderer;
   }

   private void drawWindow(int x, int y, int w, int h, int bgColour) {
      drawRect(x - 3, y - 4, x + w + 3, y - 3, bgColour);
      drawRect(x - 3, y + h + 3, x + w + 3, y + h + 4, bgColour);
      drawRect(x - 3, y - 3, x + w + 3, y + h + 3, bgColour);
      drawRect(x - 4, y - 3, x - 3, y + h + 3, bgColour);
      drawRect(x + w + 3, y - 3, x + w + 4, y + h + 3, bgColour);
   }

   public void drawWindowWithBorder(int x, int y, int w, int h, int bgColour, int frameColour) {
      this.drawWindow(x, y, w, h, bgColour);
      int frameFade = (frameColour & 16711422) >> 1 | frameColour & -16777216;
      this.drawGradientRect(x - 3, y - 3 + 1, x - 3 + 1, y + h + 3 - 1, frameColour, frameFade);
      this.drawGradientRect(x + w + 2, y - 3 + 1, x + w + 3, y + h + 3 - 1, frameColour, frameFade);
      this.drawGradientRect(x - 3, y - 3, x + w + 3, y - 3 + 1, frameColour, frameFade);
      this.drawGradientRect(x - 3, y + h + 2, x + w + 3, y + h + 3, frameColour, frameFade);
   }

   public void drawLineOnVerticalProgressBar(int x, int y, int w, int h, int p, int pMax, int lineColor) {
      x = x - 2;
      y = y - 2;
      w = w + 4;
      h = h + 4;
      int lineY = Math.round((float)p / (float)pMax * (float)h);
      drawRect(x, y + h - lineY, w + x, y + h - lineY + 1, lineColor);
   }

   public void drawVerticalProgressBar(int x, int y, int w, int h, int p, int bgColour, int frameColour, int progressColour) {
      this.drawProgressBar(x, y, w, h, p, bgColour, frameColour, progressColour, 1);
   }

   public void drawHorizontalProgressBar(int x, int y, int w, int h, int p, int bgColour, int frameColour, int progressColour) {
      this.drawProgressBar(x, y, w, h, p, bgColour, frameColour, progressColour, 0);
   }

   private void drawProgressBar(int x, int y, int w, int h, int p, int bgColour, int frameColour, int progressColour, int hv) {
      this.drawWindowWithBorder(x, y, w, h, bgColour, frameColour);
      x = x - 2;
      y = y - 2;
      w = w + 4;
      h = h + 4;
      switch(hv) {
      case 0:
         float pWf = (float)w / 100.0F * (float)p;
         int pW = Math.round(pWf);
         drawRect(x, y, x + pW, y + h, progressColour);
         break;
      case 1:
         float pHf = (float)h / 100.0F * (float)p;
         int pH = Math.round(pHf);
         drawRect(x, y + h - pH, w + x, h + y, progressColour);
      }

   }

   public void drawItemStack(ItemStack itemStack, int x, int y) {
      int[][] savedGLState = OpenGLHelper.saveGLState(new int[]{3008, 2896});
      RenderItem renderItem = this.mc.getRenderItem();
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      RenderHelper.enableGUIStandardItemLighting();
      GL11.glEnable(32826);
      GL11.glEnable(2929);
      renderItem.renderItemIntoGUI(itemStack, x, y);
      GL11.glDisable(32826);
      GL11.glDisable(3042);
      GL11.glPopMatrix();
      OpenGLHelper.restoreGLState(savedGLState);
   }

   public void drawItemStack(ItemStack itemStack, int x, int y, RenderItem renderItem, boolean transparent) {
      this.zLevel = 50.0F;
      renderItem.zLevel = 50.0F;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int colorOverlay = (new Color(139, 139, 139, 160)).hashCode();
      RenderHelper.enableGUIStandardItemLighting();
      renderItem.renderItemAndEffectIntoGUI(itemStack, x, y);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GlStateManager.disableDepth();
      GlStateManager.colorMask(true, true, true, false);
      if (transparent) {
         this.zLevel = 100.0F;
         renderItem.zLevel = 100.0F;
         this.drawGradientRect(x, y, x + 16, y + 16, colorOverlay, colorOverlay);
      }

      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.enableDepth();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.zLevel = 0.0F;
      renderItem.zLevel = 0.0F;
   }

   public void drawMiniItemStack(ItemStack itemStack, int x, int y) {
      int[][] savedGLState = OpenGLHelper.saveGLState(new int[]{3008, 2896});
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      this.drawItemStack(itemStack, x, y);
      GL11.glScalef(2.0F, 2.0F, 2.0F);
      GL11.glPopMatrix();
      OpenGLHelper.restoreGLState(savedGLState);
   }

   public void drawDisabledSlot(int x, int y, RenderItem renderItem) {
      this.zLevel = 100.0F;
      renderItem.zLevel = 100.0F;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int colorOverlay = (new Color(139, 139, 139, 200)).hashCode();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      GlStateManager.colorMask(true, true, true, false);
      renderItem.renderItemAndEffectIntoGUI(new ItemStack(Blocks.BARRIER), x, y);
      this.drawGradientRect(x, y, x + 16, y + 16, colorOverlay, colorOverlay);
      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.zLevel = 0.0F;
      renderItem.zLevel = 0.0F;
   }

   public void drawCenteredStringWithShadow(int x, int y, int w, String message, int Colour) {
      int messageWidth = this.fontRenderer.getStringWidth(message);
      int messageX = x + ((w >> 1) - (messageWidth >> 1));
      this.fontRenderer.drawStringWithShadow(message, (float)messageX, (float)y, Colour);
   }

   public void drawStringWithShadow(int x, int y, String message, int colour) {
      this.fontRenderer.drawStringWithShadow(message, (float)x, (float)y, colour);
   }

   public void drawCenteredString(int x, int y, int w, String message, int colour) {
      int messageWidth = this.fontRenderer.getStringWidth(message);
      int messageX = x + ((w >> 1) - (messageWidth >> 1));
      this.fontRenderer.drawString(message, messageX, y, colour);
   }

   public void renderSplitString(String str, int x, int y, int wrapWidth, int textColour) {
      int posY = y;

      for(String s : this.fontRenderer.listFormattedStringToWidth(str, wrapWidth)) {
         this.drawStringWithShadow(x, posY, s, textColour);
         posY += this.fontRenderer.FONT_HEIGHT;
      }

   }

   public List<String> getSplitString(String str, int wrapWidth) {
      return this.fontRenderer.listFormattedStringToWidth(str, wrapWidth);
   }

   public void drawPlayerHead(int x, int y) {
      ResourceLocation playerSkin = ClientHelper.player().getLocationSkin();
      this.mc.getTextureManager().bindTexture(playerSkin);
      int[][] savedGLState = OpenGLHelper.saveGLState(new int[]{3008, 2896});
      GL11.glPushMatrix();
      GL11.glScalef(1.0F, 0.5F, 1.0F);
      this.drawTexturedModalRect(x, y, 32, 64, 32, 64);
      this.drawTexturedModalRect(x, y, 160, 64, 32, 64);
      GL11.glPopMatrix();
      OpenGLHelper.restoreGLState(savedGLState);
   }

   private void drawPixels(int x, int y, int u, int v, int width, int height, int times) {
      for(int cu = u; cu < u + width; ++cu) {
         for(int cv = v; cv < v + height; ++cv) {
            for(int factor = 0; factor < times; ++factor) {
               this.drawTexturedModalRect(x + cu * 3 + factor, y + cv * 3 + factor, cu + u, cv + v, 1, 1);
            }
         }
      }

   }

   public void drawResource(ResourceLocation resource, int x, int y, int u, int v, int w, int h) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
      this.drawTexturedModalRect(x, y, u, v, w, h);
   }

   public static void renderGuiTank(FluidStack fluid, int capacity, int amount, double x, double y, double width, double height) {
      GL11.glPushAttrib(1048575);
      if (fluid != null && fluid.getFluid() != null && fluid.amount > 0) {
         EaglerTextureAtlasSprite icon = getStillTexture(fluid);
         if (icon != null) {
            int renderAmount = (int)Math.max(Math.min(height, (double)amount * height / (double)capacity), 1.0D);
            int posY = (int)(y + height - (double)renderAmount);
            bindBlockTexture();
            int color = fluid.getFluid().getColor(fluid);
            GL11.glColor3ub((byte)(color >> 16 & 255), (byte)(color >> 8 & 255), (byte)(color & 255));
            GlStateManager.enableBlend();

            for(int i = 0; (double)i < width; i += 16) {
               for(int j = 0; j < renderAmount; j += 16) {
                  int drawWidth = (int)Math.min(width - (double)i, 16.0D);
                  int drawHeight = Math.min(renderAmount - j, 16);
                  int drawX = (int)(x + (double)i);
                  int drawY = posY + j;
                  double minU = (double)icon.getMinU();
                  double maxU = (double)icon.getMaxU();
                  double minV = (double)icon.getMinV();
                  double maxV = (double)icon.getMaxV();
                  Tessellator tessellator = Tessellator.getInstance();
                  WorldRenderer tes = tessellator.getBuffer();
                  tes.begin(7, DefaultVertexFormats.POSITION_TEX);
                  tes.pos((double)drawX, (double)(drawY + drawHeight), 0.0D).tex(minU, minV + (maxV - minV) * (double)drawHeight / 16.0D).endVertex();
                  tes.pos((double)(drawX + drawWidth), (double)(drawY + drawHeight), 0.0D).tex(minU + (maxU - minU) * (double)drawWidth / 16.0D, minV + (maxV - minV) * (double)drawHeight / 16.0D).endVertex();
                  tes.pos((double)(drawX + drawWidth), (double)drawY, 0.0D).tex(minU + (maxU - minU) * (double)drawWidth / 16.0D, minV).endVertex();
                  tes.pos((double)drawX, (double)drawY, 0.0D).tex(minU, minV).endVertex();
                  tessellator.draw();
               }
            }

            GlStateManager.disableBlend();
            GL11.glPopAttrib();
         }
      }
   }

   public static void renderGuiTank(FluidTank tank, double x, double y, double width, double height) {
      renderGuiTank(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, width, height);
   }

   public static EaglerTextureAtlasSprite getStillTexture(Fluid fluid) {
      ResourceLocation iconKey = fluid.getStill();
      return iconKey == null ? null : Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(iconKey.toString());
   }

   public static EaglerTextureAtlasSprite getStillTexture(FluidStack fluid) {
      return fluid != null && fluid.getFluid() != null ? getStillTexture(fluid.getFluid()) : null;
   }

   public static void bindBlockTexture() {
      engine().bindTexture(BLOCK_TEX);
   }

   public static TextureManager engine() {
      return Minecraft.getMinecraft().renderEngine;
   }
}
