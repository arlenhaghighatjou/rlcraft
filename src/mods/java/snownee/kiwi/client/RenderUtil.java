package snownee.kiwi.client;

import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderUtil {
   public RenderUtil() {
   }

   public static void drawRepeatedModalRect(int x, int y, int width, int height, int u, int v, int uWidth, int vHeight, boolean flipX, boolean flipY) {
      for(int i = 0; i < width; i += uWidth) {
         int w = Math.min(uWidth, width - i);

         for(int j = 0; j < height; j += vHeight) {
            int h = Math.min(vHeight, height - j);
            drawFlippedModalRect(x + i, y + j, w, h, u, v, flipX, flipY, 256.0F, 256.0F);
         }
      }

   }

   public static void drawFlippedModalRect(int x, int y, int width, int height, int u, int v, boolean flipX, boolean flipY, float textureWidth, float textureHeight) {
      float scaleX = 1.0F / textureWidth;
      float scaleY = 1.0F / textureHeight;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer bufferbuilder = tessellator.getBuffer();
      float u1 = (float)u * scaleX;
      float u2 = (float)(u + width) * scaleX;
      float v1 = (float)v * scaleY;
      float v2 = (float)(v + height) * scaleY;
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)x, (double)(y + height), 0.0D).tex(flipX ? (double)u2 : (double)u1, flipY ? (double)v1 : (double)v2).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + height), 0.0D).tex(flipX ? (double)u1 : (double)u2, flipY ? (double)v1 : (double)v2).endVertex();
      bufferbuilder.pos((double)(x + width), (double)y, 0.0D).tex(flipX ? (double)u1 : (double)u2, flipY ? (double)v2 : (double)v1).endVertex();
      bufferbuilder.pos((double)x, (double)y, 0.0D).tex(flipX ? (double)u2 : (double)u1, flipY ? (double)v2 : (double)v1).endVertex();
      tessellator.draw();
   }
}
