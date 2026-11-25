package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GUIPanZoomView extends GUIView {
   public double viewX = 0.0D;
   public double viewY = 0.0D;
   protected double zoom = 1.0D;
   protected boolean panning = false;
   protected double zoomResetX = Double.MAX_VALUE;
   protected double zoomResetY = Double.MAX_VALUE;
   protected double panX = Double.MAX_VALUE;
   protected double panY = Double.MAX_VALUE;

   public GUIPanZoomView(GUIScreen screen, double width, double height, GUIElement... subElements) {
      super(screen, width, height);

      for(GUIElement element : subElements) {
         this.children.add(element);
         element.parent = this;
      }

      this.recalc(0);
      this.focus((GUIElement)null);
   }

   public GUIPanZoomView(GUIScreen screen, double x, double y, double width, double height, GUIElement... subElements) {
      super(screen, x, y, width, height);

      for(GUIElement element : subElements) {
         this.children.add(element);
         element.parent = this;
      }

      this.recalc(0);
   }

   public double viewW() {
      return this.absoluteWidth() / this.zoom;
   }

   public double viewH() {
      return this.absoluteHeight() / this.zoom;
   }

   public int viewPxX() {
      return (int)Math.round(this.viewX * (double)this.absolutePxWidth());
   }

   public int viewPxY() {
      return (int)Math.round(this.viewY * (double)this.absolutePxHeight());
   }

   public int viewPxW() {
      return (int)Math.round(this.viewW() * (double)this.screen.pxWidth);
   }

   public int viewPxH() {
      return (int)Math.round(this.viewH() * (double)this.screen.pxHeight);
   }

   public void focus(GUIElement child) {
      if (child == null) {
         double minX = Double.MAX_VALUE;
         double minY = Double.MAX_VALUE;
         double maxX = -1.7976931348623157E308D;
         double maxY = -1.7976931348623157E308D;

         for(GUIElement child2 : this.children) {
            if (child2.x < minX) {
               minX = child2.x;
            }

            if (child2.y < minY) {
               minY = child2.y;
            }

            if (child2.x + child2.width > maxX) {
               maxX = child2.x + child2.width;
            }

            if (child2.y + child2.height > maxY) {
               maxY = child2.y + child2.height;
            }
         }

         if (minX != Double.MAX_VALUE && minX != maxX) {
            this.viewX = minX;
            this.viewY = minY;
            this.zoom = 1.0D / Tools.max(maxX - minX, maxY - minY);
         } else {
            this.viewX = 0.0D;
            this.viewY = 0.0D;
            this.zoom = 1.0D;
         }

      } else {
         while(!this.children.contains(child) && child.parent != this && child.parent != null) {
            child = child.parent;
         }

         if (this.children.contains(child)) {
            this.viewX = child.x + child.width * 0.5D - this.viewW() * 0.5D;
            this.viewY = child.y + child.height * 0.5D - this.viewH() * 0.5D;
         }

      }
   }

   public GUIPanZoomView setZoom(double zoom) {
      return this.setZoom(zoom, 0.5D, 0.5D);
   }

   public GUIPanZoomView setZoom(double zoom, double originXPercent, double originYPercent) {
      this.viewX += this.viewW() * originXPercent;
      this.viewY += this.viewH() * originYPercent;
      this.zoom = zoom;
      this.viewX -= this.viewW() * originXPercent;
      this.viewY -= this.viewH() * originYPercent;
      return this;
   }

   public double getZoom() {
      return this.zoom;
   }

   public void draw() {
      GlStateManager.pushMatrix();
      GlStateManager.scale(this.zoom, this.zoom, 1.0D);
      GlStateManager.translate(-this.viewX, -this.viewY, 0.0D);
      this.drawChildren();
      GlStateManager.popMatrix();
   }

   public boolean mousePressed(int button) {
      boolean result = super.mousePressed(button);
      if (this.isMouseWithin() && button == FantasticConfig.guiSettings.zoomResetButton) {
         this.zoomResetX = this.mouseX();
         this.zoomResetY = this.mouseY();
         result = true;
      }

      if (this.isMouseWithin() && button == FantasticConfig.guiSettings.panButton) {
         this.panX = this.mouseX();
         this.panY = this.mouseY();
         result = true;
      }

      return result;
   }

   public void mouseDrag(int button) {
      super.mouseDrag(button);
      if (button == FantasticConfig.guiSettings.panButton && (this.panning || FantasticConfig.guiSettings.panButton != FantasticConfig.guiSettings.zoomResetButton || this.panX != Double.MAX_VALUE && (this.panX != this.mouseX() || this.panY != this.mouseY()))) {
         double xDif = (this.panX - this.mouseX()) * this.absoluteWidth();
         double yDif = (this.panY - this.mouseY()) * this.absoluteHeight();
         this.viewX += xDif / this.zoom;
         this.viewY += yDif / this.zoom;
         this.panning = true;
         this.panX = this.mouseX();
         this.panY = this.mouseY();
      }

   }

   public boolean mouseReleased(int button) {
      boolean result = super.mouseReleased(button);
      if (button == FantasticConfig.guiSettings.zoomResetButton && this.zoomResetX != Double.MAX_VALUE && (FantasticConfig.guiSettings.zoomResetButton != FantasticConfig.guiSettings.panButton || !this.panning)) {
         if (GUIScreen.isCtrlKeyDown() != FantasticConfig.guiSettings.zoomFocusMouse) {
            double portX = this.absoluteX();
            double portY = this.absoluteY();
            double portW = this.absoluteWidth();
            double portH = this.absoluteHeight();
            double mouseRelX = this.mouseX() - portX;
            double mouseRelY = this.mouseY() - portY;
            double mouseXPercent = mouseRelX / portW;
            double mouseYPercent = mouseRelY / portH;
            double centerX = this.viewX + this.viewW() * mouseXPercent;
            double centerY = this.viewY + this.viewH() * mouseYPercent;
            this.setZoom(1.0D);
            this.viewX = centerX - this.viewW() * 0.5D;
            this.viewY = centerY - this.viewH() * 0.5D;
         } else {
            this.setZoom(1.0D);
         }

         result = true;
         this.zoomResetX = Double.MAX_VALUE;
         this.zoomResetY = Double.MAX_VALUE;
      }

      if (button == FantasticConfig.guiSettings.panButton && this.panning) {
         result = true;
         this.panning = false;
         this.panX = Double.MAX_VALUE;
         this.panY = Double.MAX_VALUE;
      }

      return result;
   }

   public void mouseWheel(int delta) {
      if (this.isMouseWithin()) {
         if (delta > 0) {
            if (GUIScreen.isCtrlKeyDown() != FantasticConfig.guiSettings.zoomFocusMouse) {
               double portX = this.absoluteX();
               double portY = this.absoluteY();
               double portW = this.absoluteWidth();
               double portH = this.absoluteHeight();
               double mouseRelX = this.mouseX() - portX;
               double mouseRelY = this.mouseY() - portY;
               double mouseXPercent = mouseRelX / portW;
               double mouseYPercent = mouseRelY / portH;
               this.setZoom(this.zoom * FantasticConfig.guiSettings.zoomRate, mouseXPercent, mouseYPercent);
            } else {
               this.setZoom(this.zoom * FantasticConfig.guiSettings.zoomRate);
            }
         } else if (delta < 0) {
            if (GUIScreen.isCtrlKeyDown() != FantasticConfig.guiSettings.zoomFocusMouse) {
               double portX = this.absoluteX();
               double portY = this.absoluteY();
               double portW = this.absoluteWidth();
               double portH = this.absoluteHeight();
               double mouseRelX = this.mouseX() - portX;
               double mouseRelY = this.mouseY() - portY;
               double mouseXPercent = mouseRelX / portW;
               double mouseYPercent = mouseRelY / portH;
               this.setZoom(this.zoom / FantasticConfig.guiSettings.zoomRate, mouseXPercent, mouseYPercent);
            } else {
               this.setZoom(this.zoom / FantasticConfig.guiSettings.zoomRate);
            }
         }
      }

      super.mouseWheel(delta);
   }
}
