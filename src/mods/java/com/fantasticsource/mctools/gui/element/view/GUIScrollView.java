package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.renderer.GlStateManager;

public class GUIScrollView extends GUIView {
   public double internalHeight;
   public double progress = -1.0D;
   public double top;
   public double bottom;

   public GUIScrollView(GUIScreen screen, double width, double height, GUIElement... subElements) {
      super(screen, width, height);

      for(GUIElement element : subElements) {
         this.children.add(element);
         element.parent = this;
      }

      this.recalc(0);
   }

   public GUIScrollView(GUIScreen screen, double x, double y, double width, double height, GUIElement... subElements) {
      super(screen, x, y, width, height);

      for(GUIElement element : subElements) {
         this.children.add(element);
         element.parent = this;
      }

      this.recalc(0);
   }

   public GUIElement recalcThisOnly() {
      this.internalHeight = 0.0D;

      for(GUIElement element : this.children) {
         this.internalHeight = Tools.max(this.internalHeight, element.y + element.height);
      }

      this.recalc2();
      return this;
   }

   public GUIElement recalc(int subIndexChanged) {
      this.recalcAndRepositionSubElements(subIndexChanged);
      this.recalcThisOnly();
      this.postRecalc();
      return this;
   }

   protected void recalc2() {
      if (this.internalHeight <= 1.0D) {
         this.progress = -1.0D;
         this.top = 0.0D;
      } else {
         if (this.progress == -1.0D) {
            this.progress = 0.0D;
         }

         this.top = (this.internalHeight - 1.0D) * this.progress;
      }

      this.bottom = this.top + 1.0D;
   }

   public void focus(GUIElement child) {
      if (this.children.contains(child)) {
         if (this.internalHeight > this.height) {
            this.progress = Tools.min(1.0D, child.y * this.height / (this.internalHeight - this.height));
         }
      }
   }

   public void draw() {
      this.recalc2();
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0D, -this.top, 0.0D);
      this.drawChildren();
      GlStateManager.popMatrix();
   }

   public boolean mousePressed(int button) {
      this.recalc2();
      return super.mousePressed(button);
   }

   public boolean mouseReleased(int button) {
      this.recalc2();
      return super.mouseReleased(button);
   }

   public void mouseDrag(int button) {
      this.recalc2();
      super.mouseDrag(button);
   }

   public void mouseWheel(int delta) {
      this.recalc2();
      super.mouseWheel(delta);
   }
}
