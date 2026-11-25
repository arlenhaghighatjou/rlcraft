package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.utils.tooltip.TooltipUtils;
import java.util.ArrayList;

public class GuiProgressBar extends GuiControl {
   public boolean showToolTip = true;
   public double pos;
   public double max;

   public GuiProgressBar(String name, int x, int y, int width, int height, double max, double pos) {
      super(name, x, y, width, height);
      this.max = max;
      this.pos = pos;
      this.marginWidth = 0;
   }

   public double getPercent() {
      return this.pos / this.max;
   }

   public boolean hasMouseOverEffect() {
      return false;
   }

   public ArrayList<String> getTooltip() {
      if (this.showToolTip) {
         ArrayList<String> toolTip = new ArrayList();
         toolTip.add(TooltipUtils.printNumber((int)Math.round(this.pos)) + "/" + TooltipUtils.printNumber((int)Math.round(this.max)) + " (" + Math.round(this.getPercent() * 100.0D) + "%)");
         return toolTip;
      } else {
         return null;
      }
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      double percent = this.getPercent();
      int progressWidth = (int)((double)width * percent);
      if (progressWidth > 1) {
         style.getFace(this).renderStyle(helper, progressWidth, height);
      }

   }
}
