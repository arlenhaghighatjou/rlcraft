package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import java.util.Iterator;

public class GUIAutocroppedView extends GUIView {
   public GUIElement background;
   protected double padding;

   public GUIAutocroppedView(GUIScreen screen) {
      this(screen, 0.0D, (GUIElement)null);
   }

   public GUIAutocroppedView(GUIScreen screen, double padding) {
      this(screen, padding, (GUIElement)null);
   }

   public GUIAutocroppedView(GUIScreen screen, GUIElement background) {
      this(screen, 0.0D, background);
   }

   public GUIAutocroppedView(GUIScreen screen, double padding, GUIElement background) {
      super(screen, 1.0D, 1.0D);
      this.padding = Math.min(padding, 0.5D);
      this.background = background;
      if (background != null) {
         background.autoplace = false;
         this.add(background);
      }

   }

   public GUIAutocroppedView(GUIScreen screen, double x, double y) {
      this(screen, x, y, 0.0D, (GUIElement)null);
   }

   public GUIAutocroppedView(GUIScreen screen, double x, double y, double padding) {
      this(screen, x, y, padding, (GUIElement)null);
   }

   public GUIAutocroppedView(GUIScreen screen, double x, double y, GUIElement background) {
      this(screen, x, y, 0.0D, background);
   }

   public GUIAutocroppedView(GUIScreen screen, double x, double y, double padding, GUIElement background) {
      super(screen, x, y, 1.0D, 1.0D);
      this.padding = Math.min(padding, 0.5D);
      this.background = background;
      if (background != null) {
         background.autoplace = false;
         this.add(background);
      }

   }

   public GUIAutocroppedView recalc(int subIndexChanged) {
      this.width = 1.0D;
      this.height = 1.0D;
      if (this.parent == null) {
         return this;
      } else {
         this.recalcAndRepositionSubElements(0);
         this.width = 0.0D;
         this.height = 0.0D;
         label81:
         switch(this.subElementAutoplaceMethod) {
         case -1:
            double minY = Double.MAX_VALUE;

            for(GUIElement element : this.children) {
               if (element != this.background) {
                  minY = Tools.min(minY, element.y);
                  this.width = Tools.max(this.width, element.width);
                  this.height = Tools.max(this.height, element.height);
               }
            }

            this.height -= minY;
            break;
         case 0:
            Iterator var13 = this.children.iterator();

            while(true) {
               if (!var13.hasNext()) {
                  break label81;
               }

               GUIElement element = (GUIElement)var13.next();
               if (element != this.background) {
                  this.width = Tools.max(this.width, element.x + element.width);
                  this.height = Tools.max(this.height, element.y + element.height);
               }
            }
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 9:
         default:
            throw new IllegalArgumentException("Unimplemented autoplace type: " + this.subElementAutoplaceMethod);
         case 8:
            Iterator var12 = this.children.iterator();

            while(true) {
               if (!var12.hasNext()) {
                  break label81;
               }

               GUIElement element = (GUIElement)var12.next();
               if (element != this.background) {
                  this.width = Tools.max(this.width, element.width);
                  this.height = Tools.max(this.height, element.y + element.height);
               }
            }
         case 10:
            Iterator var11 = this.children.iterator();

            while(true) {
               if (!var11.hasNext()) {
                  break label81;
               }

               GUIElement element = (GUIElement)var11.next();
               if (element != this.background) {
                  this.width = Tools.max(this.width, element.width);
                  this.height = Tools.max(this.height, element.y + element.height);
               }
            }
         case 11:
            for(GUIElement element : this.children) {
               if (element != this.background) {
                  this.width = Tools.max(this.width, element.x + element.width);
                  this.height = Tools.max(this.height, element.height);
               }
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

         this.postRecalc();
         return this;
      }
   }
}
