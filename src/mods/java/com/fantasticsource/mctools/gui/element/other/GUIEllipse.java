package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.datastructures.Color;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GUIEllipse extends GUIElement {
   protected Color color;
   protected Color hoverColor;
   protected Color activeColor;
   protected TrigLookupTable sideStepper;

   public GUIEllipse(GUIScreen screen, double x, double y, double width, double height, int segments, Color color) {
      this(screen, x, y, width, height, segments, color, color, color);
   }

   public GUIEllipse(GUIScreen screen, double x, double y, double width, double height, int segments, Color color, Color hoverColor, Color activeColor) {
      super(screen, x, y, width, height);
      this.sideStepper = TrigLookupTable.getInstance(segments);
      this.setColor(color, hoverColor, activeColor);
   }

   public void setColor(Color color) {
      this.setColor(color, color, color);
   }

   public void setColor(Color color, Color hoverColor, Color activeColor) {
      this.color = color;
      this.hoverColor = hoverColor;
      this.activeColor = activeColor;
   }

   public void draw() {
      GlStateManager.disableTexture2D();
      GlStateManager.disableAlpha();
      Color color = this.active ? this.activeColor : (this.isMouseWithin() ? this.hoverColor : this.color);
      GlStateManager.glBegin(6);
      GlStateManager.color(color.rf(), color.gf(), color.bf(), color.af());
      double[] array = this.sideStepper.getArray();

      for(int i = 0; i < array.length; ++i) {
         double sin = array[i];
         double cos = array[Tools.posMod(i + (array.length >>> 2), array.length)];
         GlStateManager.glVertex3f(0.5F + (float)(cos * 0.5D), 0.5F - (float)(sin * 0.5D), 0.0F);
      }

      GlStateManager.glEnd();
      this.drawChildren();
   }
}
