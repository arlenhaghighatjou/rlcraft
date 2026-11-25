package betterquesting.api2.client.gui.resources.lines;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class BoxLine implements IGuiLine {
   private final short pattern;
   private final int scale;

   public BoxLine() {
      this(1, (short)-1);
   }

   public BoxLine(int stippleScale, short stipplePattern) {
      this.pattern = stipplePattern;
      this.scale = stippleScale;
   }

   public void drawLine(IGuiRect start, IGuiRect end, int width, IGuiColor color, float partialTick) {
      int minX = Math.min(start.getX(), end.getX());
      int minY = Math.min(start.getY(), end.getY());
      int maxX = Math.max(start.getX() + start.getWidth(), end.getX() + end.getWidth());
      int maxY = Math.max(start.getY() + start.getHeight(), end.getY() + end.getHeight());
      GlStateManager.pushMatrix();
      GlStateManager.disableTexture2D();
      GL11.glEnable(2852);
      color.applyGlColor();
      GL11.glLineWidth((float)width);
      GL11.glLineStipple(this.scale, this.pattern);
      GL11.glBegin(2);
      GL11.glVertex2i(minX, minY);
      GL11.glVertex2i(maxX, minY);
      GL11.glVertex2i(maxX, maxY);
      GL11.glVertex2i(minX, maxY);
      GL11.glEnd();
      GL11.glLineStipple(1, (short)-1);
      GL11.glLineWidth(1.0F);
      GL11.glDisable(2852);
      GlStateManager.enableTexture2D();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }
}
