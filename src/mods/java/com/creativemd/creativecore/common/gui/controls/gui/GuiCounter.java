package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.event.ControlEvent;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import net.minecraft.util.math.MathHelper;

public class GuiCounter extends GuiParent {
   public int min;
   public int max;
   public GuiTextfield textfield;

   public GuiCounter(String name, int x, int y, int width, int value, int min, int max) {
      super(name, x, y, width, 10);
      this.min = min;
      this.max = max;
      this.marginWidth = 0;
      this.setStyle(Style.emptyStyle);
      this.textfield = (new GuiTextfield("value", "" + MathHelper.clamp(value, min, max), 0, 0, width - 11, 8)).setNumbersOnly();
      this.addControl(this.textfield.setStyle(defaultStyle));
      this.addControl((new GuiButton("-", "-", width - 6, 7, 4, 1) {
         public void onClicked(int x, int y, int button) {
            GuiCounter.this.textfield.text = "" + GuiCounter.this.stepDown(GuiCounter.this.textfield.parseInteger());
            this.raiseEvent(new GuiControlChangedEvent(GuiCounter.this));
         }
      }).setStyle(defaultStyle));
      this.addControl((new GuiButton("+", "+", width - 6, 0, 4, 2) {
         public void onClicked(int x, int y, int button) {
            GuiCounter.this.textfield.text = "" + GuiCounter.this.stepUp(GuiCounter.this.textfield.parseInteger());
            this.raiseEvent(new GuiControlChangedEvent(GuiCounter.this));
         }
      }).setStyle(defaultStyle));
   }

   public boolean raiseEvent(ControlEvent event) {
      return event instanceof GuiControlChangedEvent && event.source.is("value") ? super.raiseEvent(new GuiControlChangedEvent(this)) : super.raiseEvent(event);
   }

   public int stepUp(int value) {
      return Math.min(this.max, value + 1);
   }

   public int stepDown(int value) {
      return Math.max(this.min, value - 1);
   }

   public int getValue() {
      return MathHelper.clamp(this.textfield.parseInteger(), this.min, this.max);
   }

   public void setValue(int value) {
      this.textfield.text = "" + MathHelper.clamp(value, this.min, this.max);
   }
}
