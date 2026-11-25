package com.creativemd.creativecore.common.gui;

import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.Color;

@SideOnly(Side.CLIENT)
public class GuiRenderHelper {
   public static GuiRenderHelper instance = new GuiRenderHelper(Minecraft.getMinecraft());
   public FontRenderer font;
   public GuiRenderHelper.ExtendedRenderItem itemRenderer;
   public static Minecraft mc = Minecraft.getMinecraft();

   public GuiRenderHelper(Minecraft mc) {
      this(mc.fontRenderer, new GuiRenderHelper.ExtendedRenderItem(mc));
   }

   public GuiRenderHelper(FontRenderer font, GuiRenderHelper.ExtendedRenderItem itemRenderer) {
      this.font = font;
      this.itemRenderer = itemRenderer;
   }

   public void drawLine(int x1, int y1, int x2, int y2, int color) {
      float f = (float)(color >> 24 & 255) / 255.0F;
      float f1 = (float)(color >> 16 & 255) / 255.0F;
      float f2 = (float)(color >> 8 & 255) / 255.0F;
      float f3 = (float)(color & 255) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.shadeModel(7425);
      GlStateManager.pushMatrix();
      GlStateManager.glLineWidth(4.0F);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos((double)x1, (double)y1, 0.0D).color(f1, f2, f3, f).endVertex();
      bufferbuilder.pos((double)x2, (double)y2, 0.0D).color(f1, f2, f3, f).endVertex();
      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public void drawGrayBackgroundRect(int x, int y, int width, int height) {
      int alpha = 180;
      int widthThird = width / 3;
      int heightThird = height / 3;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder vertexbuffer = tessellator.getBuffer();
      vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      vertexbuffer.pos((double)x + (double)widthThird, (double)y, 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x, (double)y, 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x, (double)y + (double)heightThird, 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)widthThird, (double)y + (double)heightThird, 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)widthThird, (double)y + (double)heightThird, 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x, (double)y + (double)heightThird, 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x, (double)y + (double)(heightThird * 2), 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)widthThird, (double)y + (double)(heightThird * 2), 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)widthThird, (double)y + (double)(heightThird * 3), 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)widthThird, (double)y + (double)(heightThird * 2), 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x, (double)y + (double)(heightThird * 2), 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x, (double)y + (double)(heightThird * 3), 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 2), (double)y, 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)widthThird, (double)y, 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)widthThird, (double)y + (double)heightThird, 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 2), (double)y + (double)heightThird, 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 2), (double)y + (double)heightThird, 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)widthThird, (double)y + (double)heightThird, 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)widthThird, (double)y + (double)(heightThird * 2), 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 2), (double)y + (double)(heightThird * 2), 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 2), (double)y + (double)(heightThird * 2), 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)widthThird, (double)y + (double)(heightThird * 2), 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)widthThird, (double)y + (double)(heightThird * 3), 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 2), (double)y + (double)(heightThird * 3), 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 2), (double)y, 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 2), (double)y + (double)heightThird, 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 3), (double)y + (double)heightThird, 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 3), (double)y, 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 3), (double)y + (double)heightThird, 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 2), (double)y + (double)heightThird, 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 2), (double)y + (double)(heightThird * 2), 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 3), (double)y + (double)(heightThird * 2), 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 3), (double)y + (double)(heightThird * 2), 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 2), (double)y + (double)(heightThird * 2), 0.0D).color(0, 0, 0, alpha).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 2), (double)y + (double)(heightThird * 3), 0.0D).color(0, 0, 0, 0).endVertex();
      vertexbuffer.pos((double)x + (double)(widthThird * 3), (double)y + (double)(heightThird * 3), 0.0D).color(0, 0, 0, 0).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public void drawHorizontalChannelMaskGradientRect(int left, int top, int right, int bottom, int colour, int mask) {
      this.drawHorizontalGradientRect(left, top, right, bottom, colour & ~mask | -16777216, colour | -16777216 | mask);
   }

   public void drawRect(double left, double top, double right, double bottom, int startColor) {
      float f = (float)(startColor >> 24 & 255) / 255.0F;
      float f1 = (float)(startColor >> 16 & 255) / 255.0F;
      float f2 = (float)(startColor >> 8 & 255) / 255.0F;
      float f3 = (float)(startColor & 255) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder vertexbuffer = tessellator.getBuffer();
      vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      vertexbuffer.pos(right, top, 0.0D).color(f1, f2, f3, f).endVertex();
      vertexbuffer.pos(left, top, 0.0D).color(f1, f2, f3, f).endVertex();
      vertexbuffer.pos(left, bottom, 0.0D).color(f1, f2, f3, f).endVertex();
      vertexbuffer.pos(right, bottom, 0.0D).color(f1, f2, f3, f).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public void drawHorizontalGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
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
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder vertexbuffer = tessellator.getBuffer();
      vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      vertexbuffer.pos((double)right, (double)top, 0.0D).color(f5, f6, f7, f4).endVertex();
      vertexbuffer.pos((double)left, (double)top, 0.0D).color(f1, f2, f3, f).endVertex();
      vertexbuffer.pos((double)left, (double)bottom, 0.0D).color(f1, f2, f3, f).endVertex();
      vertexbuffer.pos((double)right, (double)bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public void drawTexturedModalRect(ResourceLocation location, int x, int y, int textureX, int textureY, int width, int height) {
      mc.getTextureManager().bindTexture(location);
      this.drawTexturedModalRect(x, y, textureX, textureY, width, height);
   }

   public void drawTexturedModalRect(ResourceLocation location, int x, int y, int textureX, int textureY, int width, int height, int textureW, int textureH) {
      mc.getTextureManager().bindTexture(location);
      this.drawTexturedModalRect(x, y, textureX, textureY, width, height, textureW, textureH);
   }

   public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
      float f = 0.00390625F;
      float f1 = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)(x + 0), (double)(y + height), 0.0D).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + 0), 0.0D).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
      bufferbuilder.pos((double)(x + 0), (double)(y + 0), 0.0D).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
      tessellator.draw();
   }

   public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int textureW, int textureH) {
      float f = 0.00390625F;
      float f1 = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)(x + 0), (double)(y + height), 0.0D).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + textureH) * 0.00390625F)).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + height), 0.0D).tex((double)((float)(textureX + textureW) * 0.00390625F), (double)((float)(textureY + textureH) * 0.00390625F)).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + 0), 0.0D).tex((double)((float)(textureX + textureW) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
      bufferbuilder.pos((double)(x + 0), (double)(y + 0), 0.0D).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
      tessellator.draw();
   }

   public void renderColorPlate(Color color, int width, int height) {
      this.renderColorPlate(0, 0, color, width, height);
   }

   public void renderColorPlate(int x, int y, Color color, int width, int height) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
      bufferbuilder.pos((double)x, (double)height, 0.0D).endVertex();
      bufferbuilder.pos((double)width, (double)height, 0.0D).endVertex();
      bufferbuilder.pos((double)width, (double)y, 0.0D).endVertex();
      bufferbuilder.pos((double)x, (double)y, 0.0D).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void renderColorTriangle(int x1, int y1, int x2, int y2, int x3, int y3, Color color) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder vertexbuffer = tessellator.getBuffer();
      GlStateManager.enableBlend();
      GlStateManager.disableTexture2D();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
      vertexbuffer.begin(4, DefaultVertexFormats.POSITION);
      vertexbuffer.pos((double)x1, (double)y1, 0.0D).endVertex();
      vertexbuffer.pos((double)y3, (double)y3, 0.0D).endVertex();
      vertexbuffer.pos((double)x2, (double)y2, 0.0D).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }

   public static void renderBakedQuads(List<BakedQuad> baked) {
      mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.pushMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder vertexbuffer = tessellator.getBuffer();
      vertexbuffer.begin(7, DefaultVertexFormats.ITEM);

      for(int i = 0; i < baked.size(); ++i) {
         int tint = ((BakedQuad)baked.get(i)).getTintIndex();
         if (tint == 0) {
            tint = -1;
         } else {
            Color color = ColorUtils.IntToRGBA(tint);
            color.setAlpha(255);
            tint = ColorUtils.RGBAToInt(color);
         }

         LightUtil.renderQuadColor(vertexbuffer, (BakedQuad)baked.get(i), tint);
      }

      tessellator.draw();
      GlStateManager.popMatrix();
      GlStateManager.cullFace(CullFace.BACK);
      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
      mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
   }

   public int getFontHeight() {
      return this.font.FONT_HEIGHT;
   }

   public int getStringWidth(String text) {
      return this.font.getStringWidth(text);
   }

   public int getStringWidth(String[] lines) {
      int width = 0;

      for(int i = 0; i < lines.length; ++i) {
         width = Math.max(width, this.font.getStringWidth(lines[i]));
      }

      return width;
   }

   public int drawStringWithShadow(String text, int width, int height, int color) {
      return this.drawStringWithShadow(text, 0, 0, width, height, color);
   }

   public int drawStringWithShadow(String text, int x, int y, int width, int height, int color) {
      return this.drawStringWithShadow(text, x, y, width, height, color, 0);
   }

   public int drawStringWithShadow(String text, int x, int y, int width, int height, int color, int additionalWidth) {
      int completeWidth = this.font.getStringWidth(text) + additionalWidth;
      this.font.drawStringWithShadow(text, (float)(width / 2 - completeWidth / 2 + additionalWidth), (float)(height / 2 - this.getFontHeight() / 2), color);
      return completeWidth;
   }

   public void drawItemStackAndOverlay(ItemStack stack, int x, int y, int width, int height) {
      this.drawItemStack(stack, x, y, width, height);
      GlStateManager.pushMatrix();
      if (String.valueOf(stack.getCount()).length() > 3) {
         String s = String.valueOf(stack.getCount());
         GlStateManager.disableLighting();
         GlStateManager.disableDepth();
         GlStateManager.disableBlend();
         int fontWidth = this.font.getStringWidth(s);
         GlStateManager.translate((float)(this.font.FONT_HEIGHT / 2), (float)(fontWidth / 2), 0.0F);
         GlStateManager.scale(0.5D, 0.5D, 0.5D);
         GlStateManager.translate((float)(-this.font.FONT_HEIGHT / 2), (float)(-fontWidth / 2), 0.0F);
         fontWidth = fontWidth / 2;
         this.font.drawStringWithShadow(s, (float)(x + 19 - 2 - fontWidth), (float)(y + 6 + 5), 16777215);
         GlStateManager.enableLighting();
         GlStateManager.enableDepth();
         GlStateManager.enableBlend();
         this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, x, y, "");
      } else {
         this.itemRenderer.renderItemOverlays(this.font, stack, x, y);
      }

      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
   }

   public void drawItemStack(ItemStack stack, int x, int y, int width, int height, int rotation, int color) {
      this.itemRenderer.color = color;
      this.drawItemStack(stack, x, y, width, height, rotation);
      this.itemRenderer.color = -1;
   }

   public void drawItemStack(ItemStack stack, int x, int y, int width, int height) {
      this.drawItemStack(stack, x, y, width, height, 0);
   }

   public void drawItemStack(ItemStack stack, int x, int y, int width, int height, int rotation) {
      if (!stack.isEmpty()) {
         GlStateManager.pushMatrix();
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.translate((float)(x + 8), (float)(y + 8), 0.0F);
         GlStateManager.rotate((float)rotation, 0.0F, 0.0F, 1.0F);
         GlStateManager.scale((double)width / 16.0D, (double)height / 16.0D, 1.0D);
         GlStateManager.translate(-8.0F, -8.0F, -this.itemRenderer.zLevel - 50.0F);
         GlStateManager.enableDepth();
         this.itemRenderer.renderItemAndEffectIntoGUI(stack, 0, 0);
         GlStateManager.disableDepth();
         GlStateManager.popMatrix();
      }
   }

   public static class ExtendedRenderItem extends RenderItem {
      public RenderItem parent;
      private static final Method renderModel = ReflectionHelper.findMethod(RenderItem.class, "renderModel", "renderModel", new Class[]{IBakedModel.class, Integer.TYPE, ItemStack.class});
      private static final Method renderEffect = ReflectionHelper.findMethod(RenderItem.class, "renderEffect", "renderEffect", new Class[]{IBakedModel.class});
      private static final Method renderItemModelIntoGUI = ReflectionHelper.findMethod(RenderItem.class, "renderItemModelIntoGUI", "renderItemModelIntoGUI", new Class[]{ItemStack.class, Integer.TYPE, Integer.TYPE, IBakedModel.class});
      public int color = -1;

      public ExtendedRenderItem(Minecraft mc) {
         super(mc.renderEngine, (ModelManager)ReflectionHelper.getPrivateValue(Minecraft.class, mc, new String[]{"modelManager", "modelManager"}), mc.getItemColors());
         this.parent = mc.getRenderItem();
      }

      public IBakedModel getItemModelWithOverrides(ItemStack stack, World worldIn, EntityLivingBase entitylivingbaseIn) {
         return this.parent.getItemModelWithOverrides(stack, worldIn, entitylivingbaseIn);
      }

      protected void renderItemModelIntoGUI(ItemStack stack, int x, int y, IBakedModel bakedmodel) {
         if (this.color == -1) {
            try {
               renderItemModelIntoGUI.invoke(this.parent, stack, x, y, bakedmodel);
            } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var6) {
               var6.printStackTrace();
            }

         } else {
            GlStateManager.pushMatrix();
            GuiRenderHelper.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GuiRenderHelper.mc.renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.1F);
            this.setupGuiTransform2(x, y, bakedmodel.isGui3d());
            bakedmodel = ForgeHooksClient.handleCameraTransforms(bakedmodel, TransformType.GUI, false);
            if (!stack.isEmpty()) {
               GlStateManager.pushMatrix();
               GlStateManager.translate(-0.5F, -0.5F, -0.5F);
               if (bakedmodel.isBuiltInRenderer()) {
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  GlStateManager.enableRescaleNormal();
                  stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
               } else {
                  try {
                     renderModel.invoke(this.parent, bakedmodel, this.color, stack);
                  } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var8) {
                     var8.printStackTrace();
                  }

                  if (stack.hasEffect()) {
                     try {
                        renderEffect.invoke(this.parent, bakedmodel);
                     } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var7) {
                        var7.printStackTrace();
                     }
                  }
               }

               GlStateManager.popMatrix();
            }

            GlStateManager.disableAlpha();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
            GuiRenderHelper.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GuiRenderHelper.mc.renderEngine.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
         }
      }

      private void setupGuiTransform2(int xPosition, int yPosition, boolean isGui3d) {
         GlStateManager.translate((float)xPosition, (float)yPosition, 100.0F + this.zLevel);
         GlStateManager.translate(8.0F, 8.0F, 0.0F);
         GlStateManager.scale(1.0F, -1.0F, 1.0F);
         GlStateManager.scale(16.0F, 16.0F, 16.0F);
         if (isGui3d) {
            GlStateManager.enableLighting();
         } else {
            GlStateManager.disableLighting();
         }

      }
   }
}
