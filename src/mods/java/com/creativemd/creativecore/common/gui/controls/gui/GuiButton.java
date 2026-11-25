package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;

public abstract class GuiButton extends GuiClickableLabel {
   public GuiButton(String caption, int x, int y) {
      this(caption, x, y, GuiRenderHelper.instance.getStringWidth(caption));
   }

   public GuiButton(String name, String title, int x, int y) {
      this(name, title, x, y, GuiRenderHelper.instance.getStringWidth(title));
   }

   public GuiButton(String caption, int x, int y, int width) {
      this(caption, caption, x, y, width, 14);
   }

   public GuiButton(String name, String caption, int x, int y, int width) {
      this(name, caption, x, y, width, 14);
   }

   public GuiButton(String name, String caption, int x, int y, int width, int height) {
      super(name, caption, x, y, width, height, -1);
   }

   public GuiButton(String caption, int x, int y, int width, int height) {
      this(caption, caption, x, y, width, height);
   }

   public boolean hasBorder() {
      return true;
   }

   public boolean hasBackground() {
      return true;
   }
}
