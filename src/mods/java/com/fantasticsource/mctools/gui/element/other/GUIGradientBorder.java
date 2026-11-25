package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GUIGradientBorder extends GUIElement {
   public Color border;
   public Color center;
   public Color hoverBorder;
   public Color hoverCenter;
   public Color activeBorder;
   public Color activeCenter;
   protected double thickness;

   public GUIGradientBorder(GUIScreen screen, double width, double height, double borderThickness, Color border, Color center) {
      this(screen, width, height, borderThickness, border, center, border, center, border, center);
   }

   public GUIGradientBorder(GUIScreen screen, double width, double height, double borderThickness, Color border, Color center, Color hoverBorder, Color hoverCenter, Color activeBorder, Color activeCenter) {
      super(screen, width, height);
      this.thickness = borderThickness;
      this.setColors(border, center, hoverBorder, hoverCenter, activeBorder, activeCenter);
   }

   public GUIGradientBorder(GUIScreen screen, double x, double y, double width, double height, double borderThickness, Color border, Color center) {
      this(screen, x, y, width, height, borderThickness, border, center, border, center, border, center);
   }

   public GUIGradientBorder(GUIScreen screen, double x, double y, double width, double height, double borderThickness, Color border, Color center, Color hoverBorder, Color hoverCenter, Color activeBorder, Color activeCenter) {
      super(screen, x, y, width, height);
      this.thickness = borderThickness;
      this.setColors(border, center, hoverBorder, hoverCenter, activeBorder, activeCenter);
   }

   public GUIGradientBorder setThickness(double thickness) {
      this.thickness = thickness;
      return this;
   }

   public GUIGradientBorder setColors(Color border, Color center, Color hoverBorder, Color hoverCenter, Color activeBorder, Color activeCenter) {
      this.border = border;
      this.center = center;
      this.hoverBorder = hoverBorder;
      this.hoverCenter = hoverCenter;
      this.activeBorder = activeBorder;
      this.activeCenter = activeCenter;
      return this;
   }

   public void draw() {
      Color b;
      Color c;
      if (this.active) {
         b = this.activeBorder;
         c = this.activeCenter;
      } else if (this.isMouseWithin()) {
         b = this.hoverBorder;
         c = this.hoverCenter;
      } else {
         b = this.border;
         c = this.center;
      }

      GlStateManager.disableTexture2D();
      GlStateManager.disableAlpha();
      double min = Tools.min(0.5D, this.thickness) * (double)Tools.min(this.absolutePxWidth(), this.absolutePxHeight());
      double xThickness = min / (double)this.absolutePxWidth();
      double yThickness = min / (double)this.absolutePxHeight();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos(1.0D - xThickness, yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(xThickness, yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(xThickness, 1.0D - yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(1.0D - xThickness, 1.0D - yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(1.0D - xThickness, 0.0D, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(xThickness, 0.0D, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(xThickness, yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(1.0D - xThickness, yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(1.0D - xThickness, 1.0D - yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(xThickness, 1.0D - yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(xThickness, 1.0D, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(1.0D - xThickness, 1.0D, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(1.0D, yThickness, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(1.0D - xThickness, yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(1.0D - xThickness, 1.0D - yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(1.0D, 1.0D - yThickness, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(xThickness, yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(0.0D, yThickness, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(0.0D, 1.0D - yThickness, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(xThickness, 1.0D - yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(1.0D, 0.0D, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(1.0D - xThickness, 0.0D, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(1.0D - xThickness, yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(1.0D, yThickness, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(xThickness, 1.0D - yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(0.0D, 1.0D - yThickness, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(0.0D, 1.0D, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(xThickness, 1.0D, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(1.0D - xThickness, 1.0D - yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(1.0D - xThickness, 1.0D, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(1.0D, 1.0D, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(1.0D, 1.0D - yThickness, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(0.0D, yThickness, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      bufferbuilder.pos(xThickness, yThickness, 0.0D).color(c.r(), c.g(), c.b(), c.a()).endVertex();
      bufferbuilder.pos(xThickness, 0.0D, 0.0D).color(b.r(), b.g(), b.b(), b.a()).endVertex();
      tessellator.draw();
      this.drawChildren();
   }
}
