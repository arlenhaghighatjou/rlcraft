package com.endertech.minecraft.forge;

import com.endertech.minecraft.forge.units.IForgeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class ForgeRendering {
   public ForgeRendering() {
   }

   public static void setLightmapTextureCoords(int combinedLight) {
      int x = combinedLight % 65536;
      int y = combinedLight / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)x, (float)y);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void setLightmapTextureCoords(World world, BlockPos pos, int minLight) {
      int combinedLight = world.getCombinedLight(pos, minLight);
      setLightmapTextureCoords(combinedLight);
   }

   public static void renderItemLayerIn2D(int width, int height, float thickness, float minX, float minY, float maxX, float maxY, float minU, float minV, float maxU, float maxV, double layerOffsetX, double layerOffsetY, double layerOffsetZ) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.getBuffer();
      double x1 = (double)minX + layerOffsetX;
      double x2 = (double)maxX - layerOffsetX;
      double y1 = (double)minY + layerOffsetY;
      double y2 = (double)maxY - layerOffsetY;
      double z1 = 0.0D - layerOffsetZ;
      double z2 = layerOffsetZ - (double)thickness;
      GL11.glNormal3f(0.0F, 0.0F, 1.0F);
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      buffer.pos(x1, y1, z1).tex((double)minU, (double)minV).endVertex();
      buffer.pos(x2, y1, z1).tex((double)maxU, (double)minV).endVertex();
      buffer.pos(x2, y2, z1).tex((double)maxU, (double)maxV).endVertex();
      buffer.pos(x1, y2, z1).tex((double)minU, (double)maxV).endVertex();
      tessellator.draw();
      GL11.glNormal3f(0.0F, 0.0F, -1.0F);
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      buffer.pos(x1, y2, z2).tex((double)minU, (double)maxV).endVertex();
      buffer.pos(x2, y2, z2).tex((double)maxU, (double)maxV).endVertex();
      buffer.pos(x2, y1, z2).tex((double)maxU, (double)minV).endVertex();
      buffer.pos(x1, y1, z2).tex((double)minU, (double)minV).endVertex();
      tessellator.draw();
      float halfPixelWidth = 0.5F * (minU - maxU) / (float)width;
      float halfPixelHeight = 0.5F * (minV - maxV) / (float)height;
      GL11.glNormal3f(-1.0F, 0.0F, 0.0F);
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for(int i = 0; i < width; ++i) {
         double x = (double)((float)i / (float)width);
         double u = (double)minU + (double)(maxU - minU) * x - (double)halfPixelWidth;
         x = x1 + (double)i * ((double)(maxX - minX) - 2.0D * layerOffsetX) / (double)width;
         buffer.pos(x, y1, z2).tex(u, (double)minV).endVertex();
         buffer.pos(x, y1, z1).tex(u, (double)minV).endVertex();
         buffer.pos(x, y2, z1).tex(u, (double)maxV).endVertex();
         buffer.pos(x, y2, z2).tex(u, (double)maxV).endVertex();
      }

      tessellator.draw();
      GL11.glNormal3f(1.0F, 0.0F, 0.0F);
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for(int var45 = 0; var45 < width; ++var45) {
         double x = (double)((float)var45 / (float)width);
         double u = (double)minU + (double)(maxU - minU) * x - (double)halfPixelWidth;
         x = x1 + (double)((float)var45 + 1.0F) * ((double)(maxX - minX) - 2.0D * layerOffsetX) / (double)width;
         buffer.pos(x, y2, z2).tex(u, (double)maxV).endVertex();
         buffer.pos(x, y2, z1).tex(u, (double)maxV).endVertex();
         buffer.pos(x, y1, z1).tex(u, (double)minV).endVertex();
         buffer.pos(x, y1, z2).tex(u, (double)minV).endVertex();
      }

      tessellator.draw();
      GL11.glNormal3f(0.0F, 1.0F, 0.0F);
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for(int var46 = 0; var46 < height; ++var46) {
         double y = (double)((float)var46 / (float)height);
         double v = (double)minV + (double)(maxV - minV) * y - (double)halfPixelHeight;
         y = y1 + (double)((float)var46 + 1.0F) * ((double)(maxY - minY) - 2.0D * layerOffsetY) / (double)height;
         buffer.pos(x1, y, z1).tex((double)minU, v).endVertex();
         buffer.pos(x2, y, z1).tex((double)maxU, v).endVertex();
         buffer.pos(x2, y, z2).tex((double)maxU, v).endVertex();
         buffer.pos(x1, y, z2).tex((double)minU, v).endVertex();
      }

      tessellator.draw();
      GL11.glNormal3f(0.0F, -1.0F, 0.0F);
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

      for(int var47 = 0; var47 < height; ++var47) {
         double y = (double)((float)var47 / (float)height);
         double v = (double)minV + (double)(maxV - minV) * y - (double)halfPixelHeight;
         y = y1 + (double)var47 * ((double)(maxY - minY) - 2.0D * layerOffsetY) / (double)height;
         buffer.pos(x2, y, z1).tex((double)maxU, v).endVertex();
         buffer.pos(x1, y, z1).tex((double)minU, v).endVertex();
         buffer.pos(x1, y, z2).tex((double)minU, v).endVertex();
         buffer.pos(x2, y, z2).tex((double)maxU, v).endVertex();
      }

      tessellator.draw();
   }

   public static void renderBlock(double x, double y, double z, double width, double height, double length, double rotateX, double rotateY, double rotateZ, double red, double green, double blue, double alpha) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      GlStateManager.enableRescaleNormal();
      GlStateManager.rotate((float)rotateX, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate((float)rotateY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate((float)rotateZ, 0.0F, 0.0F, 1.0F);
      GlStateManager.scale(width, height, length);
      GlStateManager.color((float)red, (float)green, (float)blue, (float)alpha);
      GlStateManager.glBegin(9);
      GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
      GlStateManager.glVertex3f(-0.5F, 0.5F, 0.5F);
      GlStateManager.glVertex3f(-0.5F, 0.5F, 0.5F);
      GlStateManager.glVertex3f(0.5F, 0.5F, 0.5F);
      GlStateManager.glVertex3f(0.5F, 0.5F, -0.5F);
      GlStateManager.glVertex3f(-0.5F, 0.5F, -0.5F);
      GlStateManager.glEnd();
      GL11.glBegin(9);
      GlStateManager.glNormal3f(0.0F, 0.0F, 1.0F);
      GlStateManager.glVertex3f(0.5F, -0.5F, 0.5F);
      GlStateManager.glVertex3f(0.5F, 0.5F, 0.5F);
      GlStateManager.glVertex3f(-0.5F, 0.5F, 0.5F);
      GlStateManager.glVertex3f(-0.5F, -0.5F, 0.5F);
      GlStateManager.glEnd();
      GL11.glBegin(9);
      GlStateManager.glNormal3f(1.0F, 0.0F, 0.0F);
      GlStateManager.glVertex3f(0.5F, 0.5F, -0.5F);
      GlStateManager.glVertex3f(0.5F, 0.5F, 0.5F);
      GlStateManager.glVertex3f(0.5F, -0.5F, 0.5F);
      GlStateManager.glVertex3f(0.5F, -0.5F, -0.5F);
      GlStateManager.glEnd();
      GL11.glBegin(9);
      GlStateManager.glNormal3f(-1.0F, 0.0F, 0.0F);
      GlStateManager.glVertex3f(-0.5F, -0.5F, 0.5F);
      GlStateManager.glVertex3f(-0.5F, 0.5F, 0.5F);
      GlStateManager.glVertex3f(-0.5F, 0.5F, -0.5F);
      GlStateManager.glVertex3f(-0.5F, -0.5F, -0.5F);
      GlStateManager.glEnd();
      GL11.glBegin(9);
      GlStateManager.glNormal3f(0.0F, -1.0F, 0.0F);
      GlStateManager.glVertex3f(0.5F, -0.5F, 0.5F);
      GlStateManager.glVertex3f(-0.5F, -0.5F, 0.5F);
      GlStateManager.glVertex3f(-0.5F, -0.5F, -0.5F);
      GlStateManager.glVertex3f(0.5F, -0.5F, -0.5F);
      GlStateManager.glEnd();
      GL11.glBegin(9);
      GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F);
      GlStateManager.glVertex3f(0.5F, 0.5F, -0.5F);
      GlStateManager.glVertex3f(0.5F, -0.5F, -0.5F);
      GlStateManager.glVertex3f(-0.5F, -0.5F, -0.5F);
      GlStateManager.glVertex3f(-0.5F, 0.5F, -0.5F);
      GlStateManager.glEnd();
      GlStateManager.popMatrix();
   }

   public static class MeterIndicators {
      public static final ResourceLocation TEXTURE = ForgeLocating.itemTexture("forgeendertech", IForgeUnit.getClassRegName(ForgeRendering.MeterIndicators.class));

      public MeterIndicators() {
      }

      public static void beforeRender(float meterThickness) {
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.5F, 0.5F, 0.5F);
         GlStateManager.translate(0.0F, 0.0F, -meterThickness / 2.0F);
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         TextureManager manager = Minecraft.getMinecraft().getTextureManager();
         manager.bindTexture(TEXTURE);
      }

      public static void afterRender() {
         GlStateManager.enableLighting();
         GlStateManager.enableCull();
         GlStateManager.popMatrix();
      }
   }
}
