package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

public class GUITooltipView extends GUIAutocroppedView {
   private double offset = 0.0D;

   public GUITooltipView(GUIScreen screen) {
      super(screen, 0.0D, 0.0D, 0.1D, new GUIDarkenedBackground(screen));
      this.setSubElementAutoplaceMethod((byte)10);
   }

   public void draw() {
      this.x = this.mouseX() + this.offset;
      this.y = this.mouseY() - this.height / 2.0D;
      if (this.y + this.height > 1.0D) {
         this.y = 1.0D - this.height;
      }

      if (this.y < 0.0D) {
         this.y = 0.0D;
      }

      if (this.x + this.width > 1.0D) {
         this.x = this.mouseX() - this.width - this.offset;
      }

      if (this.x < 0.0D) {
         this.x = 1.0D - this.width;
      }

      if (this.x < 0.0D) {
         this.x = 0.0D;
      }

      this.drawChildren();
   }

   public GUITooltipView recalc(int subIndexChanged) {
      this.width = 1.0D;
      this.height = 1.0D;
      if (this.parent == null) {
         return this;
      } else {
         this.recalcAndRepositionSubElements(0);
         this.width = 0.0D;
         this.height = 0.0D;

         for(GUIElement element : this.children) {
            if (element != this.background) {
               this.width = Tools.max(this.width, element.x + element.width);
               this.height = Tools.max(this.height, element.y + element.height);
            }
         }

         this.recalcAndRepositionSubElements(0);
         double paddingPx = (double)Tools.min(this.absolutePxWidth(), this.absolutePxHeight()) * this.padding;
         double xPad = paddingPx / (double)this.parent.absolutePxWidth();
         double yPad = paddingPx / (double)this.parent.absolutePxHeight();
         this.width += xPad * 2.0D;
         this.height += yPad * 2.0D;
         xPad = xPad / this.width;
         yPad = yPad / this.height;

         for(GUIElement element : this.children) {
            if (element != this.background) {
               element.x += (0.5D - element.x) * 2.0D * xPad;
               element.y += (0.5D - element.y) * 2.0D * yPad;
               element.recalc(0);
            }
         }

         this.offset = 12.0D / (double)this.screen.width;
         this.postRecalc();
         return this;
      }
   }

   public GUIElement setTooltip(String tooltip) {
      this.clear();
      this.add(this.background);

      for(String line : Tools.fixedSplit(tooltip, "\n")) {
         this.add(new GUIText(this.screen, line, Color.YELLOW));
      }

      return this;
   }
}
