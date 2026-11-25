package betterquesting.api2.client.gui.resources.lines;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class SimpleLine implements IGuiLine {
   private final short pattern;
   private final int scale;

   public SimpleLine() {
      this(1, (short)-1);
   }

   public SimpleLine(int stippleScale, short stipplePattern) {
      this.pattern = stipplePattern;
      this.scale = stippleScale;
   }

   public void drawLine(IGuiRect start, IGuiRect end, int width, IGuiColor color, float partialTick) {
      GlStateManager.pushMatrix();
      GlStateManager.disableTexture2D();
      GL11.glEnable(2852);
      color.applyGlColor();
      GL11.glLineWidth((float)width);
      GL11.glLineStipple(this.scale, this.pattern);
      GL11.glBegin(1);
      GL11.glVertex2f((float)start.getX() + (float)start.getWidth() / 2.0F, (float)start.getY() + (float)start.getHeight() / 2.0F);
      GL11.glVertex2f((float)end.getX() + (float)end.getWidth() / 2.0F, (float)end.getY() + (float)end.getHeight() / 2.0F);
      GL11.glEnd();
      GL11.glLineStipple(1, (short)-1);
      GL11.glLineWidth(1.0F);
      GL11.glDisable(2852);
      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }
}
