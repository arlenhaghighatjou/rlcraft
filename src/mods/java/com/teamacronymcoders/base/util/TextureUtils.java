package com.teamacronymcoders.base.util;

import com.teamacronymcoders.base.client.ClientHelper;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class TextureUtils {
   protected static Minecraft mc = Minecraft.getMinecraft();

   public TextureUtils() {
   }

   public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer renderer = tessellator.getBuffer();
      renderer.begin(7, DefaultVertexFormats.BLOCK);
      mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      int color = fluid.getFluid().getColor(fluid);
      int brightness = ClientHelper.world().getCombinedLight(pos, fluid.getFluid().getLuminosity());
      pre(x, y, z);
      EaglerTextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
      EaglerTextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());
      putTexturedQuad(renderer, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.DOWN, color, brightness, false);
      putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.NORTH, color, brightness, true);
      putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.EAST, color, brightness, true);
      putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.SOUTH, color, brightness, true);
      putTexturedQuad(renderer, flowing, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.WEST, color, brightness, true);
      putTexturedQuad(renderer, still, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.UP, color, brightness, false);
      tessellator.draw();
      post();
   }

   public static void putTexturedQuad(WorldRenderer renderer, EaglerTextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, EnumFacing face, int color, int brightness, boolean flowing) {
      int l1 = brightness >> 16 & '\uffff';
      int l2 = brightness & '\uffff';
      int a = color >> 24 & 255;
      int r = color >> 16 & 255;
      int g = color >> 8 & 255;
      int b = color & 255;
      putTexturedQuad(renderer, sprite, x, y, z, w, h, d, face, r, g, b, a, l1, l2, flowing);
   }

   public static void putTexturedQuad(WorldRenderer renderer, EaglerTextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, EnumFacing face, int r, int g, int b, int a, int light1, int light2, boolean flowing) {
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

   public static void pre(double x, double y, double z) {
      GlStateManager.pushMatrix();
      GlStateManager.disableLighting();
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
      GlStateManager.enableLighting();
      GlStateManager.popMatrix();
   }

   public static EaglerTextureAtlasSprite getSprite(ResourceLocation textureLocation) {
      return mc.getTextureMapBlocks().getTextureExtry(textureLocation.toString());
   }
}
