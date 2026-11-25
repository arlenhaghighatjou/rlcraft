package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import org.lwjgl.util.Color;

public class GuiColoredSteppedSlider extends GuiSteppedSlider {
   public GuiColorPicker picker;
   public ColorUtils.ColorPart part;

   public GuiColoredSteppedSlider(String name, int x, int y, int width, int height, GuiColorPicker picker, ColorUtils.ColorPart part) {
      super(name, x, y, width, height, part.getColor(picker.color), 0, 255);
      this.picker = picker;
      this.part = part;
   }

   public void setValue(double value) {
      super.setValue((double)((int)value));
      if (this.part != null) {
         this.part.setColor(this.picker.color, (int)this.value);
         this.picker.onColorChanged();
      }

   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      if (this.part == ColorUtils.ColorPart.ALPHA) {
         Color startColor = new Color(this.picker.color);
         startColor.setAlpha(0);
         Color endColor = new Color(this.picker.color);
         endColor.setAlpha(255);
         helper.drawHorizontalGradientRect(0, 0, width, height, ColorUtils.RGBAToInt(startColor), ColorUtils.RGBAToInt(endColor));
      } else {
         helper.drawHorizontalChannelMaskGradientRect(0, 0, width, height, ColorUtils.RGBAToInt(this.picker.color), this.part.getBrightest());
      }

      super.renderContent(helper, style, width, height);
   }
}
