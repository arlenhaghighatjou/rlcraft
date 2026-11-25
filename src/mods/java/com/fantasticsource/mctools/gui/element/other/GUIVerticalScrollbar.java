package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

public class GUIVerticalScrollbar extends GUIGradientBorder {
   private GUIGradientBorder slider;
   private GUIScrollView scrollView;

   public GUIVerticalScrollbar(GUIScreen screen, double width, double height, Color backgroundBorder, Color backgroundCenter, Color sliderBorder, Color sliderCenter, GUIScrollView scrollView) {
      super(screen, width, height, 0.3333333333333333D, backgroundBorder, backgroundCenter);
      this.scrollView = scrollView;
      this.slider = new GUIGradientBorder(screen, 0.0D, -9999.0D, 1.0D, 0.1D, 0.3333333333333333D, sliderBorder, sliderCenter);
      this.add(this.slider);
   }

   public GUIVerticalScrollbar(GUIScreen screen, double x, double y, double width, double height, Color backgroundBorder, Color backgroundCenter, Color sliderBorder, Color sliderCenter, GUIScrollView scrollView) {
      super(screen, x, y, width, height, 0.3333333333333333D, backgroundBorder, backgroundCenter);
      this.scrollView = scrollView;
      this.slider = new GUIGradientBorder(screen, 0.0D, -9999.0D, 1.0D, 0.1D, 0.3333333333333333D, sliderBorder, sliderCenter);
      this.add(this.slider);
   }

   public void draw() {
      if (this.scrollView.progress >= 0.0D && this.scrollView.progress <= 1.0D) {
         this.slider.y = (1.0D - this.slider.height) * this.scrollView.progress;
      } else {
         this.slider.y = -99999.0D;
      }

      super.draw();
   }

   public void mouseWheel(int delta) {
      if (this.scrollView.progress != -1.0D && (this.isMouseWithin() || this.scrollView.isMouseWithin())) {
         if (delta < 0) {
            this.scrollView.progress += 0.25D / this.scrollView.internalHeight;
            if (this.scrollView.progress > 1.0D) {
               this.scrollView.progress = 1.0D;
            }
         } else {
            this.scrollView.progress -= 0.25D / this.scrollView.internalHeight;
            if (this.scrollView.progress < 0.0D) {
               this.scrollView.progress = 0.0D;
            }
         }
      }

   }

   public boolean mousePressed(int button) {
      boolean result = super.mousePressed(button);
      this.setActive(result);
      if (this.active && this.scrollView.progress != -1.0D) {
         this.scrollView.progress = Tools.min(Tools.max((this.mouseY() - this.absoluteY() - this.slider.absoluteHeight() * 0.5D) / (this.absoluteHeight() - this.slider.absoluteHeight()), 0.0D), 1.0D);
      }

      return result;
   }

   public void mouseDrag(int button) {
      if (this.active && button == 0) {
         if (this.scrollView.progress == -1.0D) {
            this.setActive(false);
         } else {
            this.scrollView.progress = Tools.min(Tools.max((this.mouseY() - this.absoluteY() - this.slider.absoluteHeight() * 0.5D) / (this.absoluteHeight() - this.slider.absoluteHeight()), 0.0D), 1.0D);
         }
      }

   }
}
