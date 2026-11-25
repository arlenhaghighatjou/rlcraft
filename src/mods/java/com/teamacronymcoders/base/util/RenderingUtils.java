package com.teamacronymcoders.base.util;

import com.teamacronymcoders.base.client.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderingUtils {
   public static Minecraft mc = Minecraft.getMinecraft();
   public static float FLUID_OFFSET = 0.005F;

   public RenderingUtils() {
   }

   public static void render3DItem(EntityItem item, float angle) {
      GL11.glPushMatrix();
      item.hoverStart = 0.0F;
      GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
      mc.getRenderManager().renderEntity(item, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, false);
      GL11.glPopMatrix();
   }

   private static void setupGlTranslucent() {
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glShadeModel(7425);
      GL11.glDisable(3008);
      GL11.glDisable(2884);
      RenderHelper.disableStandardItemLighting();
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
   }

   private static void renderTranslucent(BufferBuilder vertex, double startX, double startY, double startZ, double endX, double endY, double endZ, double rotationX, double rotationY, double rotationZ) {
      GL11.glPushMatrix();
      GL11.glRotated(rotationX, 1.0D, 0.0D, 0.0D);
      GL11.glRotated(rotationY, 0.0D, 1.0D, 0.0D);
      GL11.glRotated(rotationZ, 0.0D, 0.0D, 1.0D);
      GL11.glPushMatrix();

      for(int j = 0; j < 4; ++j) {
         drawTranslucent(vertex, startX, startY, startZ, endX, endY, endZ);
         GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
      }

      GL11.glPopMatrix();
      GL11.glPopMatrix();
   }

   private static void drawTranslucent(BufferBuilder vertex, double startX, double startY, double startZ, double endX, double endY, double endZ) {
      vertex.begin(7, DefaultVertexFormats.POSITION_COLOR);
      vertex.pos(-startX, startY, -startZ).color(100, 0, 0, 255).endVertex();
      vertex.pos(startX, startY, -startZ).color(0, 100, 0, 255).endVertex();
      vertex.pos(endX, endY, -endZ).color(0, 0, 100, 255).endVertex();
      vertex.pos(-endX, endY, -endZ).color(40, 20, 255, 255).endVertex();
      Tessellator.getInstance().draw();
   }

   public static void renderBeamAt(Entity entity, double startX, double startY, double startZ, double endX, double endY, double endZ, double rotationX, double rotationY, double rotationZ) {
      GL11.glPushMatrix();
      GL11.glTranslated(startX, startY, startZ);
      GL11.glPushAttrib(1048575);
      setupGlTranslucent();
      GL11.glTranslatef(0.5F, 0.0F, 0.5F);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder vertex = tessellator.getBuffer();
      renderTranslucent(vertex, startX, startY, startZ, endX, endY, endZ, rotationX, rotationY, rotationZ);
      GL11.glPopAttrib();
      GL11.glPopMatrix();
   }

   public static void drawLine(double x, double y, double x2, double y2, float red, float green, float blue, float lineWidth) {
      int existed = ClientHelper.player().ticksExisted;
      float alpha = 0.3F + MathHelper.sin((float)((double)existed + x)) * 0.3F + 0.3F;
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buff = tess.getBuffer();
      GL11.glPushMatrix();
      GL11.glPushAttrib(1048575);
      GL11.glLineWidth(lineWidth);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 1);
      buff.begin(3, DefaultVertexFormats.POSITION_COLOR);
      buff.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
      buff.pos(x2, y2, 0.0D).color(red, green, blue, alpha).endVertex();
      tess.draw();
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(32826);
      GL11.glEnable(3553);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopAttrib();
      GL11.glPopMatrix();
   }

   public static void drawLine(double x, double y, double z, double x2, double y2, double z2, float red, float green, float blue, float lineWidth) {
      int count = ClientHelper.player().ticksExisted;
      float alpha = 0.3F + MathHelper.sin((float)((double)count + x)) * 0.3F + 0.3F;
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buff = tess.getBuffer();
      GL11.glPushMatrix();
      GL11.glLineWidth(lineWidth);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 1);
      buff.begin(3, DefaultVertexFormats.POSITION_COLOR);
      buff.pos(x, y, z).color(red, green, blue, alpha).endVertex();
      buff.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
      tess.draw();
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(32826);
      GL11.glEnable(3553);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
   }

   public static void drawLineNoFade(double x, double y, double x2, double y2, float red, float green, float blue, float lineWidth, float alpha) {
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buff = tess.getBuffer();
      GL11.glPushMatrix();
      GL11.glLineWidth(lineWidth);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      buff.begin(3, DefaultVertexFormats.POSITION_COLOR);
      buff.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
      buff.pos(x2, y2, 0.0D).color(red, green, blue, alpha).endVertex();
      tess.draw();
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(32826);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
   }

   public static void drawLineNoFade(double x, double y, double z, double x2, double y2, double z2, float red, float green, float blue, float lineWidth, float alpha) {
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buff = tess.getBuffer();
      GL11.glPushMatrix();
      GL11.glLineWidth(lineWidth);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      buff.begin(3, DefaultVertexFormats.POSITION_COLOR);
      buff.pos(x, y, z).color(red, green, blue, alpha).endVertex();
      buff.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
      tess.draw();
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(32826);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
   }

   public static void drawLine(double x, double y, double x2, double y2, float red, float green, float blue, float lineWidth, float fadeSpeed) {
      int count = ClientHelper.player().ticksExisted;
      float alpha = fadeSpeed + MathHelper.sin((float)((double)count + x)) * 0.3F + 0.3F;
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buff = tess.getBuffer();
      GL11.glPushMatrix();
      GL11.glLineWidth(lineWidth);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      buff.begin(3, DefaultVertexFormats.POSITION_COLOR);
      buff.pos(x, y, 0.0D).color(red, green, blue, alpha).endVertex();
      buff.pos(x2, y2, 0.0D).color(red, green, blue, alpha).endVertex();
      tess.draw();
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(32826);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
   }

   public static void drawLine(double x, double y, double z, double x2, double y2, double z2, float red, float green, float blue, float lineWidth, float fadeSpeed) {
      int existed = ClientHelper.player().ticksExisted;
      float alpha = 0.3F + MathHelper.sin((float)((double)existed + x)) * 0.3F + 0.3F;
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buff = tess.getBuffer();
      GL11.glPushMatrix();
      GL11.glLineWidth(lineWidth);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 1);
      buff.begin(3, DefaultVertexFormats.POSITION_COLOR);
      buff.pos(x, y, z).color(red, green, blue, alpha).endVertex();
      buff.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
      tess.draw();
      GL11.glBlendFunc(770, 771);
      GL11.glDisable(32826);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
   }

   public static void drawRect(double x, double y, double width, double height, float red, float green, float blue, float lineWidth) {
      drawLine(x, y, x + width, y, red, green, blue, lineWidth, 0.0F);
      drawLine(x + width, y, x + width, y + width, red, green, blue, lineWidth, 0.0F);
      drawLine(x + width, y + width, x, y + width, red, green, blue, lineWidth, 0.0F);
      drawLine(x, y + width, x, y, red, green, blue, lineWidth, 0.0F);
   }

   public static void drawRectFadeless(double x, double y, double width, double height, float red, float green, float blue, float lineWidth, float alpha) {
      drawLineNoFade(x, y, x + width, y, red, green, blue, lineWidth, alpha);
      drawLineNoFade(x + width, y, x + width, y + width, red, green, blue, lineWidth, alpha);
      drawLineNoFade(x + width, y + width, x, y + width, red, green, blue, lineWidth, alpha);
      drawLineNoFade(x, y + width, x, y, red, green, blue, lineWidth, alpha);
   }

   public static void drawRect(double x, double y, double width, double height, float red, float green, float blue, float lineWidth, float fadeSpeed) {
      drawLine(x, y, x + width, y, red, green, blue, lineWidth, fadeSpeed);
      drawLine(x + width, y, x + width, y + width, red, green, blue, lineWidth, fadeSpeed);
      drawLine(x + width, y + width, x, y + width, red, green, blue, lineWidth, fadeSpeed);
      drawLine(x, y + width, x, y, red, green, blue, lineWidth, fadeSpeed);
   }

   public static void drawRect(double x, double y, double z, double width, double height, float red, float green, float blue, float lineWidth) {
      drawLine(x, y, z, x + width, y, z, red, green, blue, lineWidth);
      drawLine(x + width, y, z, x + width, y + width, z, red, green, blue, lineWidth);
      drawLine(x + width, y + width, z, x, y + width, z, red, green, blue, lineWidth);
      drawLine(x, y + width, z, x, y, z, red, green, blue, lineWidth);
   }

   public static void drawRectNoFade(double x, double y, double z, double width, double height, float red, float green, float blue, float lineWidth, float alpha) {
      drawLineNoFade(x, y, z, x + width, y, z, red, green, blue, lineWidth, alpha);
      drawLineNoFade(x + width, y, z, x + width, y + width, z, red, green, blue, lineWidth, alpha);
      drawLineNoFade(x + width, y + width, z, x, y + width, z, red, green, blue, lineWidth, alpha);
      drawLineNoFade(x, y + width, z, x, y, z, red, green, blue, lineWidth, alpha);
   }

   public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double w, double h, double d) {
      double wd = (1.0D - w) / 2.0D;
      double hd = (1.0D - h) / 2.0D;
      double dd = (1.0D - d) / 2.0D;
      renderFluidCuboid(fluid, pos, x, y, z, wd, hd, dd, 1.0D - wd, 1.0D - hd, 1.0D - dd);
   }

   public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2) {
      int color = fluid.getFluid().getColor(fluid);
      renderFluidCuboid(fluid, pos, x, y, z, x1, y1, z1, x2, y2, z2, color);
   }

   public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder renderer = tessellator.getBuffer();
      renderer.begin(7, DefaultVertexFormats.BLOCK);
      mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      int brightness = mc.world.getCombinedLight(pos, fluid.getFluid().getLuminosity());
      pre(x, y, z);
      TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
      TextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());
      putTexturedQuad(renderer, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.DOWN, color, brightness, false);
      putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.NORTH, color, brightness, true);
      putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.EAST, color, brightness, true);
      putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.SOUTH, color, brightness, true);
      putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.WEST, color, brightness, true);
      putTexturedQuad(renderer, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.UP, color, brightness, false);
      tessellator.draw();
      post();
   }

   public static void renderStackedFluidCuboid(FluidStack fluid, double px, double py, double pz, BlockPos pos, BlockPos from, BlockPos to, double ymin, double ymax) {
      renderStackedFluidCuboid(fluid, px, py, pz, pos, from, to, ymin, ymax, FLUID_OFFSET);
   }

   public static void renderStackedFluidCuboid(FluidStack fluid, double px, double py, double pz, BlockPos pos, BlockPos from, BlockPos to, double ymin, double ymax, float offsetToBlockEdge) {
      if (ymin < ymax) {
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder renderer = tessellator.getBuffer();
         renderer.begin(7, DefaultVertexFormats.BLOCK);
         mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         int color = fluid.getFluid().getColor(fluid);
         int brightness = mc.world.getCombinedLight(pos, fluid.getFluid().getLuminosity());
         pre(px, py, pz);
         GlStateManager.translate((float)from.getX(), (float)from.getY(), (float)from.getZ());
         TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
         TextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());
         if (still == null) {
            still = mc.getTextureMapBlocks().getMissingSprite();
         }

         if (flowing == null) {
            flowing = mc.getTextureMapBlocks().getMissingSprite();
         }

         int xd = to.getX() - from.getX();
         int yminInt = (int)ymin;
         int yd = (int)(ymax - (double)yminInt);
         if (ymax % 1.0D == 0.0D) {
            --yd;
         }

         int zd = to.getZ() - from.getZ();
         double xmin = (double)offsetToBlockEdge;
         double xmax = (double)xd + 1.0D - (double)offsetToBlockEdge;
         double zmin = (double)offsetToBlockEdge;
         double zmax = (double)zd + 1.0D - (double)offsetToBlockEdge;
         double[] xs = new double[2 + xd];
         double[] ys = new double[2 + yd];
         double[] zs = new double[2 + zd];
         xs[0] = xmin;

         for(int i = 1; i <= xd; ++i) {
            xs[i] = (double)i;
         }

         xs[xd + 1] = xmax;
         ys[0] = ymin;

         for(int i = 1; i <= yd; ++i) {
            ys[i] = (double)(i + yminInt);
         }

         ys[yd + 1] = ymax;
         zs[0] = zmin;

         for(int i = 1; i <= zd; ++i) {
            zs[i] = (double)i;
         }

         zs[zd + 1] = zmax;

         for(int y = 0; y <= yd; ++y) {
            for(int z = 0; z <= zd; ++z) {
               for(int x = 0; x <= xd; ++x) {
                  double x1 = xs[x];
                  double x2 = xs[x + 1] - x1;
                  double y1 = ys[y];
                  double y2 = ys[y + 1] - y1;
                  double z1 = zs[z];
                  double z2 = zs[z + 1] - z1;
                  if (x == 0) {
                     putTexturedQuad(renderer, flowing, x1, y1, z1, x2, y2, z2, EnumFacing.WEST, color, brightness, true);
                  }

                  if (x == xd) {
                     putTexturedQuad(renderer, flowing, x1, y1, z1, x2, y2, z2, EnumFacing.EAST, color, brightness, true);
                  }

                  if (y == 0) {
                     putTexturedQuad(renderer, still, x1, y1, z1, x2, y2, z2, EnumFacing.DOWN, color, brightness, false);
                  }

                  if (y == yd) {
                     putTexturedQuad(renderer, still, x1, y1, z1, x2, y2, z2, EnumFacing.UP, color, brightness, false);
                  }

                  if (z == 0) {
                     putTexturedQuad(renderer, flowing, x1, y1, z1, x2, y2, z2, EnumFacing.NORTH, color, brightness, true);
                  }

                  if (z == zd) {
                     putTexturedQuad(renderer, flowing, x1, y1, z1, x2, y2, z2, EnumFacing.SOUTH, color, brightness, true);
                  }
               }
            }
         }

         tessellator.draw();
         post();
      }
   }

   public static void putTexturedCuboid(BufferBuilder renderer, ResourceLocation location, double x1, double y1, double z1, double x2, double y2, double z2, int color, int brightness) {
      boolean flowing = false;
      TextureAtlasSprite sprite = mc.getTextureMapBlocks().getTextureExtry(location.toString());
      putTexturedQuad(renderer, sprite, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.DOWN, color, brightness, flowing);
      putTexturedQuad(renderer, sprite, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.NORTH, color, brightness, flowing);
      putTexturedQuad(renderer, sprite, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.EAST, color, brightness, flowing);
      putTexturedQuad(renderer, sprite, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.SOUTH, color, brightness, flowing);
      putTexturedQuad(renderer, sprite, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.WEST, color, brightness, flowing);
      putTexturedQuad(renderer, sprite, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.UP, color, brightness, flowing);
   }

   public static void putTexturedQuad(BufferBuilder renderer, TextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, EnumFacing face, int color, int brightness, boolean flowing) {
      int l1 = brightness >> 16 & '\uffff';
      int l2 = brightness & '\uffff';
      int a = color >> 24 & 255;
      int r = color >> 16 & 255;
      int g = color >> 8 & 255;
      int b = color & 255;
      putTexturedQuad(renderer, sprite, x, y, z, w, h, d, face, r, g, b, a, l1, l2, flowing);
   }

   public static void putTexturedQuad(BufferBuilder renderer, TextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, EnumFacing face, int r, int g, int b, int a, int light1, int light2, boolean flowing) {
      if (sprite != null) {
         double size = 16.0D;
         if (flowing) {
            size = 8.0D;
         }

         double x2 = x + w;
         double y2 = y + h;
         double z2 = z + d;
         double xt1 = x % 1.0D;

         double xt2;
         for(xt2 = xt1 + w; xt2 > 1.0D; --xt2) {
            ;
         }

         double yt1 = y % 1.0D;

         double yt2;
         for(yt2 = yt1 + h; yt2 > 1.0D; --yt2) {
            ;
         }

         double zt1 = z % 1.0D;

         double zt2;
         for(zt2 = zt1 + d; zt2 > 1.0D; --zt2) {
            ;
         }

         if (flowing) {
            double tmp = 1.0D - yt1;
            yt1 = 1.0D - yt2;
            yt2 = tmp;
         }

         double minU;
         double maxU;
         double minV;
         double maxV;
         switch(face) {
         case DOWN:
         case UP:
            minU = (double)sprite.getInterpolatedU(xt1 * size);
            maxU = (double)sprite.getInterpolatedU(xt2 * size);
            minV = (double)sprite.getInterpolatedV(zt1 * size);
            maxV = (double)sprite.getInterpolatedV(zt2 * size);
            break;
         case NORTH:
         case SOUTH:
            minU = (double)sprite.getInterpolatedU(xt2 * size);
            maxU = (double)sprite.getInterpolatedU(xt1 * size);
            minV = (double)sprite.getInterpolatedV(yt1 * size);
            maxV = (double)sprite.getInterpolatedV(yt2 * size);
            break;
         case WEST:
         case EAST:
            minU = (double)sprite.getInterpolatedU(zt2 * size);
            maxU = (double)sprite.getInterpolatedU(zt1 * size);
            minV = (double)sprite.getInterpolatedV(yt1 * size);
            maxV = (double)sprite.getInterpolatedV(yt2 * size);
            break;
         default:
            minU = (double)sprite.getMinU();
            maxU = (double)sprite.getMaxU();
            minV = (double)sprite.getMinV();
            maxV = (double)sprite.getMaxV();
         }

         switch(face) {
         case DOWN:
            renderer.pos(x, y, z).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
            renderer.pos(x2, y, z).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
            renderer.pos(x2, y, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
            renderer.pos(x, y, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
            break;
         case UP:
            renderer.pos(x, y2, z).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
            renderer.pos(x, y2, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
            renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
            renderer.pos(x2, y2, z).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
            break;
         case NORTH:
            renderer.pos(x, y, z).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
            renderer.pos(x, y2, z).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
            renderer.pos(x2, y2, z).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
            renderer.pos(x2, y, z).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
            break;
         case SOUTH:
            renderer.pos(x, y, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
            renderer.pos(x2, y, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
            renderer.pos(x2, y2, z2).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
            renderer.pos(x, y2, z2).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
            break;
         case WEST:
            renderer.pos(x, y, z).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
            renderer.pos(x, y, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
            renderer.pos(x, y2, z2).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
            renderer.pos(x, y2, z).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
            break;
         case EAST:
            renderer.pos(x2, y, z).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
            renderer.pos(x2, y2, z).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
            renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
            renderer.pos(x2, y, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
         }

      }
   }

   public static void pre(double x, double y, double z) {
      GlStateManager.pushMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      if (Minecraft.isAmbientOcclusionEnabled()) {
         GL11.glShadeModel(7425);
      } else {
         GL11.glShadeModel(7424);
      }

      GlStateManager.translate(x, y, z);
   }

   public static void post() {
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
      RenderHelper.enableStandardItemLighting();
   }

   public static void setColorRGB(int color) {
      setColorRGBA(color | -16777216);
   }

   public static void setColorRGBA(int color) {
      float a = (float)alpha(color) / 255.0F;
      float r = (float)red(color) / 255.0F;
      float g = (float)green(color) / 255.0F;
      float b = (float)blue(color) / 255.0F;
      GlStateManager.color(r, g, b, a);
   }

   public static void setBrightness(BufferBuilder renderer, int brightness) {
      renderer.putBrightness4(brightness, brightness, brightness, brightness);
   }

   public static int compose(int r, int g, int b, int a) {
      int rgb = (a << 8) + r;
      rgb = (rgb << 8) + g;
      rgb = (rgb << 8) + b;
      return rgb;
   }

   public static int alpha(int c) {
      return c >> 24 & 255;
   }

   public static int red(int c) {
      return c >> 16 & 255;
   }

   public static int green(int c) {
      return c >> 8 & 255;
   }

   public static int blue(int c) {
      return c & 255;
   }

   public static float[] directionalVelocitiesOfMagnitude(Vec3i vector, float magnitude) {
      float x = 0.0F;
      float y = 0.0F;
      float z = 0.0F;
      if (vector.getX() != 0) {
         x = magnitude;
         if (vector.getX() < 0) {
            x = -magnitude;
         }
      }

      if (vector.getY() != 0) {
         y = magnitude;
         if (vector.getY() < 0) {
            y = -magnitude;
         }
      }

      if (vector.getZ() != 0) {
         z = magnitude;
         if (vector.getZ() < 0) {
            z = -magnitude;
         }
      }

      return new float[]{x, y, z};
   }
}
