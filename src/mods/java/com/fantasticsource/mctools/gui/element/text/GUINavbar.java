package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

public class GUINavbar extends GUITextButton {
   public GUINavbar(GUIScreen screen) {
      this(screen, 1.0D);
   }

   public GUINavbar(GUIScreen screen, double scale) {
      this(screen, Color.AQUA, scale);
   }

   public GUINavbar(GUIScreen screen, Color color) {
      this(screen, color, 1.0D);
   }

   public GUINavbar(GUIScreen screen, Color color, double scale) {
      this(screen, color, Color.BLANK, scale);
   }

   public GUINavbar(GUIScreen screen, Color border, Color center) {
      this(screen, border, center, 1.0D);
   }

   public GUINavbar(GUIScreen screen, Color border, Color center, double scale) {
      super(screen, genText(screen), border, center, scale);
      this.setSubElementAutoplaceMethod((byte)8);
      this.width = 1.0D;
      GUIGradientBorder back = (GUIGradientBorder)this.background;
      back.border = back.activeBorder;
      back.hoverBorder = back.activeBorder;
      GUIText fore = (GUIText)this.children.get(1);
      fore.setText(genText(screen));
      fore.setColor(back.border);
   }

   private static String genText(GUIScreen screen) {
      StringBuilder result = new StringBuilder();

      for(GUIScreen.ScreenEntry entry : GUIScreen.SCREEN_STACK) {
         if (result.toString().equals("")) {
            result = new StringBuilder(entry.screen.title());
         } else {
            result.append(" > ").append(entry.screen.title());
         }
      }

      if (result.toString().equals("")) {
         return screen.title();
      } else {
         return result.append(" > ").append(screen.title()).toString();
      }
   }

   public GUINavbar recalc(int subIndexChanged) {
      if (this.size() > 1) {
         GUIText fore = (GUIText)this.children.get(1);
         fore.text = genText(this.screen);
      }

      this.width = 1.0D;
      this.height = 1.0D;
      if (this.parent == null) {
         return this;
      } else {
         this.recalcAndRepositionSubElements(0);
         this.height = 0.0D;

         for(GUIElement element : this.children) {
            if (element != this.background) {
               this.height = Tools.max(this.height, element.y + element.height);
            }
         }

         this.recalcAndRepositionSubElements(0);
         double paddingPx = (double)Tools.min(this.absolutePxWidth(), this.absolutePxHeight()) * this.padding;
         double xPad = paddingPx / (double)this.parent.absolutePxWidth();
         double yPad = paddingPx / (double)this.parent.absolutePxHeight();
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

         this.postRecalc();
         return this;
      }
   }

   public void draw() {
      if (this.screen.isVisible()) {
         super.draw();
      }

   }
}
