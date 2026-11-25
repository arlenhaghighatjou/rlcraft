package com.creativemd.creativecore.common.gui;

import org.lwjgl.util.Color;

public class Rect {
   public int minX;
   public int minY;
   public int maxX;
   public int maxY;

   public Rect(Rect rect) {
      this(rect.minX, rect.minY, rect.maxX, rect.maxY);
   }

   public Rect(int minX, int minY, int maxX, int maxY) {
      this.minX = minX;
      this.minY = minY;
      this.maxX = maxX;
      this.maxY = maxY;
   }

   public Rect mergeRects(Rect otherRect) {
      return new Rect(Math.max(this.minX, otherRect.minX), Math.max(this.minY, otherRect.minY), Math.min(this.maxX, otherRect.maxX), Math.min(this.maxY, otherRect.maxY));
   }

   public void renderRect(GuiRenderHelper helper, Color color) {
      helper.renderColorPlate(this.minX, this.minY, color, this.maxX, this.maxY);
   }

   public Rect getOffsetRect(int x, int y) {
      return new Rect(this.minX + x, this.minY + y, this.maxX + x, this.maxY + y);
   }

   public void scale(float scale) {
      if (scale != 1.0F) {
         this.minX = (int)((float)this.minX * scale);
         this.minY = (int)((float)this.minY * scale);
         this.maxX = (int)((float)this.maxX * scale);
         this.maxY = (int)((float)this.maxY * scale);
      }
   }
}
