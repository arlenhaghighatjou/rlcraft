package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import net.minecraft.util.math.MathHelper;

public class GuiCounterDecimal extends GuiParent {
   public float min;
   public float max;
   public GuiTextfield textfield;

   public GuiCounterDecimal(String name, int x, int y, int width, float value, float min, float max) {
      super(name, x, y, width, 10);
      this.min = min;
      this.max = max;
      this.marginWidth = 0;
      this.setStyle(Style.emptyStyle);
      this.textfield = (new GuiTextfield("value", "" + MathHelper.clamp(value, min, max), 0, 0, width - 11, 8)).setNumbersOnly();
      this.addControl(this.textfield.setStyle(defaultStyle));
      this.addControl((new GuiButton("-", "-", width - 6, 7, 4, 1) {
         public void onClicked(int x, int y, int button) {
            GuiCounterDecimal.this.textfield.text = "" + Float.toString(GuiCounterDecimal.this.stepDown(GuiCounterDecimal.this.textfield.parseFloat()));
         }
      }).setStyle(defaultStyle));
      this.addControl((new GuiButton("+", "+", width - 6, 0, 4, 2) {
         public void onClicked(int x, int y, int button) {
            GuiCounterDecimal.this.textfield.text = "" + Float.toString(GuiCounterDecimal.this.stepUp(GuiCounterDecimal.this.textfield.parseFloat()));
         }
      }).setStyle(defaultStyle));
   }

   public float stepUp(float value) {
      return Math.min(this.max, value + 1.0F);
   }

   public float stepDown(float value) {
      return Math.max(this.min, value - 1.0F);
   }

   public float getValue() {
      return MathHelper.clamp(this.textfield.parseFloat(), this.min, this.max);
   }

   public void setValue(float value) {
      this.textfield.text = "" + Float.toString(MathHelper.clamp(value, this.min, this.max));
   }
}
