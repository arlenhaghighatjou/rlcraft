package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.window.Window;

public class ElementButtonTooltip extends ElementButton {
   public String tooltip;

   public ElementButtonTooltip(Window window, int x, int y, int w, int h, int ID, boolean igMin, int sideH, int sideV, String Text, String tip) {
      super(window, x, y, w, h, ID, igMin, sideH, sideV, Text);
      this.tooltip = tip;
   }

   public String tooltip() {
      return this.tooltip;
   }
}
