package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Collision;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GUILine extends GUIElement {
   protected static final int TOLERANCE_PX = 5;
   protected Color color;
   protected Color hoverColor;
   protected Color activeColor;
   protected boolean isDownRight;
   public float thickness;
   protected double x1;
   protected double y1;
   protected double x2;
   protected double y2;

   public GUILine(GUIScreen screen, double x1, double y1, double x2, double y2, Color color) {
      this(screen, x1, y1, x2, y2, color, color, color, 1.0F);
   }

   public GUILine(GUIScreen screen, double x1, double y1, double x2, double y2, Color color, float thickness) {
      this(screen, x1, y1, x2, y2, color, color, color, thickness);
   }

   public GUILine(GUIScreen screen, double x1, double y1, double x2, double y2, Color color, Color hoverColor, Color activeColor) {
      this(screen, x1, y1, x2, y2, color, hoverColor, activeColor, 1.0F);
   }

   public GUILine(GUIScreen screen, double x1, double y1, double x2, double y2, Color color, Color hoverColor, Color activeColor, float thickness) {
      super(screen, Tools.min(x1, x2), Tools.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
      this.isDownRight = x1 < x2 == y1 < y2;
      this.setColor(color, hoverColor, activeColor);
      this.thickness = thickness;
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
   }

   public void setColor(Color color) {
      this.setColor(color, color, color);
   }

   public void setColor(Color color, Color hoverColor, Color activeColor) {
      this.color = color;
      this.hoverColor = hoverColor;
      this.activeColor = activeColor;
   }

   public double getX1() {
      return this.x1;
   }

   public double getY1() {
      return this.y1;
   }

   public double getX2() {
      return this.x2;
   }

   public double getY2() {
      return this.y2;
   }

   public boolean isWithin(double x, double y) {
      double[] nearest = this.isDownRight ? Collision.pointSegmentNearest(x, y, this.absoluteX(), this.absoluteY(), this.absoluteX() + this.absoluteWidth(), this.absoluteY() + this.absoluteHeight()) : Collision.pointSegmentNearest(x, y, this.absoluteX() + this.absoluteWidth(), this.absoluteY(), this.absoluteX(), this.absoluteY() + this.absoluteHeight());
      return Tools.distanceSquared(nearest[0] * (double)this.screen.pxWidth, nearest[1] * (double)this.screen.pxHeight, x * (double)this.screen.pxWidth, y * (double)this.screen.pxHeight) < (double)((this.thickness + 5.0F) * (this.thickness + 5.0F));
   }

   public void draw() {
      GlStateManager.disableTexture2D();
      GlStateManager.disableAlpha();
      Color color = this.active ? this.activeColor : (this.isMouseWithin() ? this.hoverColor : this.color);
      GlStateManager.glLineWidth(this.thickness);
      GlStateManager.glBegin(1);
      GlStateManager.color(color.rf(), color.gf(), color.bf(), color.af());
      if (this.isDownRight) {
         GlStateManager.glVertex3f(0.0F, 0.0F, 0.0F);
         GlStateManager.glVertex3f(1.0F, 1.0F, 0.0F);
      } else {
         GlStateManager.glVertex3f(0.0F, 1.0F, 0.0F);
         GlStateManager.glVertex3f(1.0F, 0.0F, 0.0F);
      }

      GlStateManager.glEnd();
      GlStateManager.glLineWidth(1.0F);
      this.drawChildren();
   }
}
