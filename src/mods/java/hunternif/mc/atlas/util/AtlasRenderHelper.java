package hunternif.mc.atlas.util;

import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class AtlasRenderHelper {
   public AtlasRenderHelper() {
   }

   public static void drawTexturedRect(ResourceLocation texture, double x, double y, double u, double v, int width, int height, int imageWidth, int imageHeight, double scaleX, double scaleY) {
      Minecraft.getMinecraft().renderEngine.bindTexture(texture);
      double minU = u / (double)imageWidth;
      double maxU = (u + (double)width) / (double)imageWidth;
      double minV = v / (double)imageHeight;
      double maxV = (v + (double)height) / (double)imageHeight;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer renderer = tessellator.getBuffer();
      renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      renderer.pos((double)((int)(x + scaleX * (double)width)), (double)((int)(y + scaleY * (double)height)), 0.0D).tex(maxU, maxV).endVertex();
      renderer.pos((double)((int)(x + scaleX * (double)width)), (double)((int)y), 0.0D).tex(maxU, minV).endVertex();
      renderer.pos((double)((int)x), (double)((int)y), 0.0D).tex(minU, minV).endVertex();
      renderer.pos((double)((int)x), (double)((int)(y + scaleY * (double)height)), 0.0D).tex(minU, maxV).endVertex();
      tessellator.draw();
   }

   public static void drawTexturedRect(ResourceLocation texture, double x, double y, int u, int v, int width, int height, int imageWidth, int imageHeight) {
      drawTexturedRect(texture, x, y, (double)u, (double)v, width, height, imageWidth, imageHeight, 1.0D, 1.0D);
   }

   private static void drawFullTexture(ResourceLocation texture, double x, double y, int width, int height, double scaleX, double scaleY) {
      drawTexturedRect(texture, x, y, 0.0D, 0.0D, width, height, width, height, scaleX, scaleY);
   }

   public static void drawFullTexture(ResourceLocation texture, double x, double y, int width, int height) {
      drawFullTexture(texture, x, y, width, height, 1.0D, 1.0D);
   }

   public static void drawAutotileCorner(ResourceLocation texture, int x, int y, double u, double v, int tileHalfSize) {
      Minecraft.getMinecraft().renderEngine.bindTexture(texture);
      double minU = u / 4.0D;
      double maxU = (u + 1.0D) / 4.0D;
      double minV = v / 6.0D;
      double maxV = (v + 1.0D) / 6.0D;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer renderer = tessellator.getBuffer();
      renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      renderer.pos((double)(x + tileHalfSize), (double)(y + tileHalfSize), 0.0D).tex(maxU, maxV).endVertex();
      renderer.pos((double)(x + tileHalfSize), (double)y, 0.0D).tex(maxU, minV).endVertex();
      renderer.pos((double)x, (double)y, 0.0D).tex(minU, minV).endVertex();
      renderer.pos((double)x, (double)(y + tileHalfSize), 0.0D).tex(minU, maxV).endVertex();
      tessellator.draw();
   }

   public static void setGLColor(int color, float alpha) {
      float r = (float)(color >> 16 & 255) / 256.0F;
      float g = (float)(color >> 8 & 255) / 256.0F;
      float b = (float)(color & 255) / 256.0F;
      GlStateManager.color(r, g, b, alpha);
   }
}
