package lumien.custommainmenu.util;

import org.lwjgl.opengl.GL11;

public class RenderUtil {
   public RenderUtil() {
   }

   public static void drawCompleteImage(int posX, int posY, int width, int height) {
      GL11.glPushMatrix();
      GL11.glTranslatef((float)posX, (float)posY, 0.0F);
      GL11.glBegin(7);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(0.0F, (float)height, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f((float)width, (float)height, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f((float)width, 0.0F, 0.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
   }

   public static void drawPartialImage(int posX, int posY, int imageX, int imageY, int width, int height, int imagePartWidth, int imagePartHeight) {
      double imageWidth = (double)GL11.glGetTexLevelParameteri(3553, 0, 4096);
      double imageHeight = (double)GL11.glGetTexLevelParameteri(3553, 0, 4097);
      double einsTeilerWidth = 1.0D / imageWidth;
      double uvWidth = einsTeilerWidth * (double)imagePartWidth;
      double uvX = einsTeilerWidth * (double)imageX;
      double einsTeilerHeight = 1.0D / imageHeight;
      double uvHeight = einsTeilerHeight * (double)imagePartHeight;
      double uvY = einsTeilerHeight * (double)imageY;
      GL11.glPushMatrix();
      GL11.glTranslatef((float)posX, (float)posY, 0.0F);
      GL11.glBegin(7);
      GL11.glTexCoord2d(uvX, uvY);
      GL11.glVertex3f(0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2d(uvX, uvY + uvHeight);
      GL11.glVertex3f(0.0F, (float)height, 0.0F);
      GL11.glTexCoord2d(uvX + uvWidth, uvY + uvHeight);
      GL11.glVertex3f((float)width, (float)height, 0.0F);
      GL11.glTexCoord2d(uvX + uvWidth, uvY);
      GL11.glVertex3f((float)width, 0.0F, 0.0F);
      GL11.glEnd();
      GL11.glPopMatrix();
   }
}
