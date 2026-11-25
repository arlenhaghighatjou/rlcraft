package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import net.minecraft.init.SoundEvents;

public class GuiAnalogeSlider extends GuiControl {
   public double maxValue;
   public double minValue;
   public double value;
   public boolean grabbedSlider;
   public int sliderWidth = 4;
   protected GuiTextfield textfield = null;

   public GuiAnalogeSlider(String name, int x, int y, int width, int height, double value, double minValue, double maxValue) {
      super(name, x, y, width, height);
      this.marginWidth = 0;
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.setValue(value);
   }

   public String getTextByValue() {
      return (float)Math.round(this.value * 100.0D) / 100.0F + "";
   }

   public String getTextfieldValue() {
      return this.getTextByValue();
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      double percent = this.getPercentage();
      int posX = (int)((double)(this.width - (this.borderWidth * 2 + this.sliderWidth)) * percent);
      style.getFace(this).renderStyle(posX, 0, helper, 4, height);
      if (this.textfield != null) {
         this.textfield.renderControl(helper, 1.0F, this.getRect());
      } else {
         helper.drawStringWithShadow(this.getTextByValue(), width, height, -1);
      }

   }

   public double getPercentage() {
      return (this.value - this.minValue) / (this.maxValue - this.minValue);
   }

   public boolean mousePressed(int x, int y, int button) {
      if (button == 0) {
         if (this.textfield != null) {
            return this.textfield.mousePressed(x, y, button);
         } else {
            playSound(SoundEvents.UI_BUTTON_CLICK);
            return this.grabbedSlider = true;
         }
      } else if (button == 1) {
         this.grabbedSlider = false;
         this.textfield = this.createTextfield();
         this.textfield.focused = true;
         this.textfield.setCursorPosition(this.textfield.text.length());
         this.textfield.parent = this.parent;
         return true;
      } else {
         return false;
      }
   }

   protected GuiTextfield createTextfield() {
      return (new GuiTextfield(this.getTextfieldValue(), 0, 0, this.width - this.getContentOffset() * 8, this.height - this.getContentOffset() * 8)).setFloatOnly();
   }

   public void closeTextField() {
      double value = this.value;

      try {
         this.setValue((double)Float.parseFloat(this.textfield.text));
         playSound(SoundEvents.UI_BUTTON_CLICK);
      } catch (NumberFormatException var4) {
         this.setValue(value);
      }

      this.textfield = null;
   }

   public boolean onKeyPressed(char character, int key) {
      if (this.textfield != null) {
         if (key == 28) {
            this.closeTextField();
            return true;
         } else {
            return this.textfield.onKeyPressed(character, key);
         }
      } else {
         return super.onKeyPressed(character, key);
      }
   }

   public void setValue(double value) {
      this.value = Math.max(this.minValue, value);
      this.value = Math.min(this.maxValue, this.value);
      this.raiseEvent(new GuiControlChangedEvent(this));
   }

   public void mouseMove(int posX, int posY, int button) {
      if (this.grabbedSlider) {
         int width = this.width - this.getContentOffset() * 2 - this.sliderWidth;
         if (posX < this.posX + this.getContentOffset()) {
            this.value = this.minValue;
         } else if (posX > this.posX + this.getContentOffset() + width + this.sliderWidth / 2) {
            this.value = this.maxValue;
         } else {
            int mouseOffsetX = posX - this.posX - this.getContentOffset() - this.sliderWidth / 2;
            this.value = (double)((float)(this.minValue + (double)((float)((this.maxValue - this.minValue) * (double)((float)mouseOffsetX / (float)width)))));
         }

         this.setValue(this.value);
      }

   }

   public void onLoseFocus() {
      if (this.textfield != null) {
         this.closeTextField();
      }

      super.onLoseFocus();
   }

   public void mouseReleased(int posX, int posY, int button) {
      if (this.grabbedSlider) {
         this.grabbedSlider = false;
      }

   }
}
