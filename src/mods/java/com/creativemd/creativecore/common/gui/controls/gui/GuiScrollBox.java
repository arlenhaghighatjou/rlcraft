package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.utils.math.SmoothValue;
import net.minecraft.init.SoundEvents;

public class GuiScrollBox extends GuiParent {
   public int maxScroll;
   public SmoothValue scrolled;
   public float scaleFactor;
   public boolean dragged;
   public int scrollbarWidth;

   public GuiScrollBox(String name, int x, int y, int width, int height) {
      this(name, x, y, width, height, 1.0F);
   }

   public GuiScrollBox(String name, int x, int y, int width, int height, float scaleFactor) {
      super(name, x, y, width, height);
      this.maxScroll = 0;
      this.scrolled = new SmoothValue(200L);
      this.scrollbarWidth = 6;
      this.scaleFactor = scaleFactor;
      this.marginWidth = 0;
   }

   public float getScaleFactor() {
      return this.scaleFactor;
   }

   public double getOffsetY() {
      return -this.scrolled.current();
   }

   public void onScrolled() {
      if (this.scrolled.aimed() < 0.0D) {
         this.scrolled.set(0.0D);
      }

      if (this.scrolled.aimed() > (double)this.maxScroll) {
         this.scrolled.set((double)this.maxScroll);
      }

   }

   public boolean mouseScrolled(int x, int y, int scrolled) {
      if (super.mouseScrolled(x, y, scrolled)) {
         return true;
      } else {
         this.scrolled.set(this.scrolled.aimed() - (double)(scrolled * 20));
         this.onScrolled();
         return true;
      }
   }

   public boolean mousePressed(int x, int y, int button) {
      if (button == 0 && this.width - x + this.posX <= this.scrollbarWidth && this.needsScrollbar()) {
         playSound(SoundEvents.UI_BUTTON_CLICK);
         this.dragged = true;
         return true;
      } else {
         return super.mousePressed(x, y, button);
      }
   }

   public void mouseMove(int x, int y, int button) {
      if (this.dragged) {
         double percent = (double)(y - this.posY) / (double)this.height;
         this.scrolled.set((double)((int)(percent * (double)this.maxScroll)));
         this.onScrolled();
      }

      super.mouseMove(x, y, button);
   }

   public void mouseReleased(int x, int y, int button) {
      super.mouseReleased(x, y, button);
      this.dragged = false;
   }

   public boolean needsScrollbar() {
      return this.lastRenderedHeight > this.height - this.getContentOffset() * 2;
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      super.renderContent(helper, style, width - this.scrollbarWidth, height);
      style.getBorder(this).renderStyle(width - this.scrollbarWidth, 0, helper, this.scrollbarWidth, height);
      style.getMouseOverBackground(this).renderStyle(width - this.scrollbarWidth + 1, 0, helper, this.scrollbarWidth - 1, height);
      this.scrolled.tick();
      int scrollThingHeight = Math.max(10, Math.min(height, this.lastRenderedHeight / height / height));
      if (this.lastRenderedHeight < height) {
         scrollThingHeight = height;
      }

      double percent = this.scrolled.current() / (double)this.maxScroll;
      style.getFace(this).renderStyle(width - this.scrollbarWidth + 1, (int)(percent * (double)(height - scrollThingHeight)), helper, this.scrollbarWidth - 1, scrollThingHeight);
      this.maxScroll = Math.max(0, this.lastRenderedHeight - height) + 5;
   }

   public boolean hasMouseOverEffect() {
      return false;
   }
}
