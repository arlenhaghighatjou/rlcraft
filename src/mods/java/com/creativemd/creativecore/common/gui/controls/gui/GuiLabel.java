package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;

public class GuiLabel extends GuiControl {
   protected String caption;
   public int color;

   public GuiLabel(String caption, int x, int y) {
      this(caption, x, y, -1);
   }

   public GuiLabel(String name, String caption, int x, int y) {
      this(name, caption, x, y, -1);
   }

   public GuiLabel(String name, String caption, int x, int y, int color) {
      this(name, caption, x, y, GuiRenderHelper.instance.getStringWidth(caption), GuiRenderHelper.instance.getFontHeight(), color);
   }

   public GuiLabel(String caption, int x, int y, int color) {
      this(caption, caption, x, y, color);
   }

   public GuiLabel(String caption, int x, int y, int width, int height, int color) {
      this(caption, caption, x, y, width, height, color);
   }

   public GuiLabel(String name, String caption, int x, int y, int width, int height, int color) {
      super(name, x, y, width, height);
      this.width += this.getAdditionalSize();
      this.color = color;
      this.caption = caption;
   }

   public String getCaption() {
      return this.caption;
   }

   public void setCaption(String caption) {
      this.caption = caption;
      this.width = font.getStringWidth(caption) + this.getContentOffset() * 2;
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      if (this.shouldDrawTitle()) {
         helper.drawStringWithShadow(this.caption, 0, 0, width, height, this.getColor(), this.getAdditionalSize());
      }

   }

   protected int getAdditionalSize() {
      return 0;
   }

   public boolean hasBorder() {
      return false;
   }

   public boolean hasBackground() {
      return false;
   }

   public boolean shouldDrawTitle() {
      return true;
   }

   public int getColor() {
      return this.color;
   }
}
