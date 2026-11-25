package lumien.custommainmenu.util;

import lumien.custommainmenu.lib.textures.ITexture;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderUtil {
   public static void drawCompleteImage(int x, int y, int width, int height) {
      drawImage(x, y, width, height, 0.0D, 0.0D, 1.0D, 1.0D);
   }

   public static void drawPartialImage(ITexture texture, int x, int y, int imageX, int imageY,
                                      int width, int height, int imagePartWidth, int imagePartHeight) {
      double u = (double)imageX / texture.getWidth();
      double v = (double)imageY / texture.getHeight();
      double uEnd = u + (double)imagePartWidth / texture.getWidth();
      double vEnd = v + (double)imagePartHeight / texture.getHeight();
      drawImage(x, y, width, height, u, v, uEnd, vEnd);
   }

   public static void drawImage(int x, int y, int width, int height, double u, double v, double uEnd, double vEnd) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer buffer = tessellator.getBuffer();
      buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
      buffer.pos(x, y, 0.0D).tex(u, v).endVertex();
      buffer.pos(x, y + height, 0.0D).tex(u, vEnd).endVertex();
      buffer.pos(x + width, y + height, 0.0D).tex(uEnd, vEnd).endVertex();
      buffer.pos(x + width, y, 0.0D).tex(uEnd, v).endVertex();
      tessellator.draw();
   }
}
