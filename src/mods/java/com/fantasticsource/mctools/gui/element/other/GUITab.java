package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;

public class GUITab extends GUIElement {
   private double tabX;
   private double tabY;

   public GUITab(GUIScreen screen, double tabX, double tabY) {
      super(screen, 0.0D, 0.0D);
      this.tabX = tabX;
      this.tabY = tabY;
   }

   public GUITab(GUIScreen screen, double tabX, double tabY, byte subElementAutoplaceMethod) {
      super(screen, 0.0D, 0.0D, subElementAutoplaceMethod);
      this.tabX = tabX;
      this.tabY = tabY;
   }

   public GUITab recalc(int subIndexChanged) {
      this.width = this.tabX - this.x;
      this.height = this.tabY - this.y;
      this.recalcAndRepositionSubElements(subIndexChanged);
      this.postRecalc();
      return this;
   }
}
