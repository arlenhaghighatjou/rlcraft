package slimeknights.mantle.client.gui;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class GuiWidgetSlider extends GuiWidget {
   public final GuiElement slider;
   public final GuiElement sliderHighlighted;
   public final GuiElement sliderDisabled;
   public final GuiElement slideBarTop;
   public final GuiElement slideBarBottom;
   public final GuiElementScalable slideBar;
   protected int minValue;
   protected int maxValue;
   protected int increment;
   protected int currentValue;
   public int sliderOffset;
   protected boolean enabled;
   protected boolean hidden;
   protected boolean isScrolling;
   protected boolean isHighlighted;
   private int clickX;
   private int clickY;
   private boolean clickedBar;

   public GuiWidgetSlider(GuiElement slider, GuiElement sliderHighlighted, GuiElement sliderDisabled, GuiElement slideBarTop, GuiElement slideBarBottom, GuiElementScalable slideBar) {
      this.slider = slider;
      this.sliderHighlighted = sliderHighlighted;
      this.sliderDisabled = sliderDisabled;
      this.slideBar = slideBar;
      this.slideBarTop = slideBarTop;
      this.slideBarBottom = slideBarBottom;
      this.height = slideBar.h;
      this.width = slideBar.w;
      this.currentValue = this.minValue = 0;
      this.maxValue = slideBar.h;
      this.increment = 1;
      this.sliderOffset = MathHelper.abs(slideBar.w - slider.w) / 2;
      this.isScrolling = false;
      this.isHighlighted = false;
      this.enabled = true;
      this.hidden = false;
   }

   public void setSize(int height) {
      this.height = height;
   }

   public void setSliderParameters(int min, int max, int stepsize) {
      this.minValue = min;
      this.maxValue = max;
      this.increment = stepsize;
      this.setSliderValue(this.currentValue);
   }

   public int getValue() {
      return this.isHidden() ? 0 : Math.min(this.maxValue, Math.max(this.minValue, this.currentValue));
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void enable() {
      this.enabled = true;
   }

   public void disable() {
      this.enabled = false;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setHidden(boolean hidden) {
      this.hidden = hidden;
   }

   public void hide() {
      this.hidden = true;
   }

   public void show() {
      this.hidden = false;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void draw() {
      if (!this.hidden) {
         this.slideBarTop.draw(this.xPos, this.yPos);
         this.slideBar.drawScaledY(this.xPos, this.yPos + this.slideBarTop.h, this.getUsableSlidebarHeight());
         this.slideBarBottom.draw(this.xPos, this.yPos + this.height - this.slideBarBottom.h);
         int x = this.xPos + this.sliderOffset;
         int y = this.yPos + this.getSliderTop();
         if (this.enabled) {
            if (this.isScrolling) {
               this.sliderDisabled.draw(x, y);
            } else if (this.isHighlighted) {
               this.sliderHighlighted.draw(x, y);
            } else {
               this.slider.draw(x, y);
            }
         } else {
            this.sliderDisabled.draw(x, y);
         }

      }
   }

   public void update(int mouseX, int mouseY, boolean useMouseWheel) {
      if (this.enabled && !this.hidden) {
         boolean mouseDown = Mouse.isButtonDown(0);
         int wheel = Mouse.getDWheel();
         if (useMouseWheel) {
            if (wheel > 0) {
               this.decrement();
               return;
            }

            if (wheel < 0) {
               this.increment();
               return;
            }
         }

         int x = mouseX - this.xPos;
         int y = mouseY - this.yPos;
         if (!mouseDown && this.clickedBar) {
            this.clickedBar = false;
         }

         if (!mouseDown && this.isScrolling) {
            this.isScrolling = false;
         } else if (this.isScrolling) {
            float d = (float)(this.maxValue - this.minValue);
            float val = (float)(y - this.clickY) / (float)(this.getUsableSlidebarHeight() - this.slider.h);
            val = val * d;
            if (val < (float)this.increment / 2.0F) {
               this.setSliderValue(this.minValue);
            } else if (val > (float)this.maxValue - (float)this.increment / 2.0F) {
               this.setSliderValue(this.maxValue);
            } else {
               this.setSliderValue((int)((float)this.minValue + (float)this.increment * (float)Math.round(val)));
            }
         } else if (x >= 0 && y >= this.getSliderTop() && x - this.sliderOffset <= this.slider.w && y <= this.getSliderTop() + this.slider.h) {
            this.isHighlighted = true;
            if (mouseDown) {
               this.isScrolling = true;
               this.clickX = x - this.sliderOffset;
               this.clickY = y - this.getSliderTop();
            }
         } else if (mouseDown && !this.clickedBar && x >= 0 && y >= 0 && x <= this.slideBar.w && y <= this.height) {
            if (y < this.getSliderTop()) {
               this.decrement();
            } else {
               this.increment();
            }

            this.clickedBar = true;
         } else {
            this.isHighlighted = false;
         }

      }
   }

   public int increment() {
      this.setSliderValue(this.currentValue + this.increment);
      return this.currentValue;
   }

   public int decrement() {
      this.setSliderValue(this.currentValue - this.increment);
      return this.currentValue;
   }

   public int setSliderValue(int val) {
      if (val > this.maxValue) {
         val = this.maxValue;
      } else if (val < this.minValue) {
         val = this.minValue;
      }

      this.currentValue = val;
      return this.currentValue;
   }

   private int getSliderTop() {
      float d = (float)(this.maxValue - this.minValue);
      d = (float)(this.currentValue - this.minValue) / d;
      d = d * (float)(this.getUsableSlidebarHeight() - this.slider.h);
      return (int)d + this.slideBarTop.h;
   }

   private int getUsableSlidebarHeight() {
      return this.height - this.slideBarTop.h - this.slideBarBottom.h;
   }
}
