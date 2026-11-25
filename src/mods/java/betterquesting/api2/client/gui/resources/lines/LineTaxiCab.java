package betterquesting.api2.client.gui.resources.lines;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class LineTaxiCab implements IGuiLine {
   private final float bias;
   private final boolean isVertical;
   private final short stipMask;
   private final int stipScale;

   public LineTaxiCab() {
      this(0.5F, false, 1, (short)-1);
   }

   public LineTaxiCab(float bias, boolean isVertical, int stipScale, short stipMask) {
      this.bias = MathHelper.clamp(bias, 0.0F, 1.0F);
      this.isVertical = isVertical;
      this.stipScale = stipScale;
      this.stipMask = stipMask;
   }

   public void drawLine(IGuiRect start, IGuiRect end, int width, IGuiColor color, float partialTick) {
      GlStateManager.pushMatrix();
      GlStateManager.disableTexture2D();
      GL11.glEnable(2852);
      color.applyGlColor();
      GL11.glLineWidth((float)width);
      GL11.glLineStipple(this.stipScale, this.stipMask);
      int x1 = start.getX() + start.getWidth() / 2;
      int y1 = start.getY() + start.getHeight() / 2;
      int x2 = end.getX() + end.getWidth() / 2;
      int y2 = end.getY() + end.getHeight() / 2;
      int x3 = x1 + Math.round((float)(x2 - x1) * this.bias);
      int y3 = y1 + Math.round((float)(y2 - y1) * this.bias);
      if (this.bias > 0.0F) {
         GL11.glBegin(1);
         GL11.glVertex2f((float)x1, (float)y1);
         if (this.isVertical) {
            GL11.glVertex2f((float)x1, (float)y3);
         } else {
            GL11.glVertex2f((float)x3, (float)y1);
         }

         GL11.glEnd();
      }

      if (this.isVertical) {
         GL11.glBegin(1);
         GL11.glVertex2f((float)x1, (float)y3);
         GL11.glVertex2f((float)x2, (float)y3);
         GL11.glEnd();
      } else {
         GL11.glBegin(1);
         GL11.glVertex2f((float)x3, (float)y1);
         GL11.glVertex2f((float)x3, (float)y2);
         GL11.glEnd();
      }

      if (this.bias < 1.0F) {
         GL11.glBegin(1);
         if (this.isVertical) {
            GL11.glVertex2f((float)x2, (float)y3);
         } else {
            GL11.glVertex2f((float)x3, (float)y2);
         }

         GL11.glVertex2f((float)x2, (float)y2);
         GL11.glEnd();
      }

      GL11.glLineStipple(1, (short)-1);
      GL11.glLineWidth(1.0F);
      GL11.glDisable(2852);
      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }
}
