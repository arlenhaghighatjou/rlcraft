package com.creativemd.creativecore.common.gui.controls.gui;

import org.apache.commons.lang3.ArrayUtils;

public class GuiArraySlider extends GuiSteppedSlider {
   public String[] values;

   public GuiArraySlider(String name, int x, int y, int width, int height, String value, String... values) {
      super(name, x, y, width, height, ArrayUtils.indexOf(values, value), 0, values.length - 1);
      this.values = values;
   }

   public String getTextByValue() {
      return this.value > (double)this.values.length ? "" : this.values[(int)this.value] + "";
   }

   public void select(String value) {
      this.setValue((double)ArrayUtils.indexOf(this.values, value));
   }

   public void setValues(String[] values) {
      this.minValue = 0.0D;
      this.maxValue = (double)(values.length - 1);
      this.values = values;
      this.setValue(0.0D);
   }

   public String getValue() {
      return this.values[(int)this.value];
   }
}
