package com.creativemd.creativecore.common.gui.controls.gui;

import net.minecraft.client.gui.GuiScreen;

public class GuiSteppedSlider extends GuiAnalogeSlider {
   public GuiSteppedSlider(String name, int x, int y, int width, int height, int value, int min, int max) {
      super(name, x, y, width, height, (double)value, (double)min, (double)max);
   }

   public String getTextByValue() {
      return (int)this.value + "";
   }

   public String getTextfieldValue() {
      return (int)this.value + "";
   }

   public boolean mouseScrolled(int x, int y, int scrolled) {
      this.setValue(this.value + (double)((GuiScreen.isCtrlKeyDown() ? 10 : 1) * (scrolled > 0 ? 1 : -1)));
      return true;
   }

   public void mouseMove(int posX, int posY, int button) {
      super.mouseMove(posX, posY, button);
      this.value = (double)((int)this.value);
   }

   public void setValue(double value) {
      super.setValue((double)((int)value));
   }

   protected GuiTextfield createTextfield() {
      return super.createTextfield().setNumbersIncludingNegativeOnly();
   }
}
