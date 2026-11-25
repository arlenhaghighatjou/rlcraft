package ivorius.ivtoolkit.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GuiSliderMultivalue extends GuiButton {
   private float[] values;
   public int mousePressedInsideIndex = -1;
   private List<GuiControlListener<GuiSliderMultivalue>> listeners = new ArrayList();
   private float minValue = 0.0F;
   private float maxValue = 1.0F;

   public GuiSliderMultivalue(int id, int x, int y, int width, int height, int values, String displayString) {
      super(id, x, y, width, height, displayString);
      this.values = new float[values];
   }

   public int getHoverState(boolean mouseHovering) {
      return 0;
   }

   protected void mouseDragged(Minecraft mc, int x, int y) {
      if (this.visible) {
         if (this.mousePressedInsideIndex >= 0) {
            this.values[this.mousePressedInsideIndex] = (float)(x - (this.x + 4)) / (float)(this.width - 8);
            this.values[this.mousePressedInsideIndex] = this.values[this.mousePressedInsideIndex] * (this.maxValue - this.minValue) + this.minValue;
            if (this.values[this.mousePressedInsideIndex] < this.minValue) {
               this.values[this.mousePressedInsideIndex] = this.minValue;
            }

            if (this.values[this.mousePressedInsideIndex] > this.maxValue) {
               this.values[this.mousePressedInsideIndex] = this.maxValue;
            }

            this.notifyOfChanges();
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

         for(float value : this.values) {
            float drawVal = (value - this.minValue) / (this.maxValue - this.minValue);
            this.drawTexturedModalRect(this.x + (int)(drawVal * (float)(this.width - 8)), this.y, 0, 66, 4, this.height);
            this.drawTexturedModalRect(this.x + (int)(drawVal * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, this.height);
         }
      }

   }

   public boolean mousePressed(Minecraft mc, int x, int y) {
      if (!super.mousePressed(mc, x, y)) {
         return false;
      } else {
         float value = (float)(x - (this.x + 4)) / (float)(this.width - 8);
         value = value * (this.maxValue - this.minValue) + this.minValue;
         float nearestDist = -1.0F;

         for(int i = 0; i < this.values.length; ++i) {
            float dist = Math.abs(this.values[i] - value);
            if (dist < nearestDist || nearestDist < 0.0F) {
               this.mousePressedInsideIndex = i;
               nearestDist = dist;
            }
         }

         this.values[this.mousePressedInsideIndex] = value;
         if (this.values[this.mousePressedInsideIndex] < this.minValue) {
            this.values[this.mousePressedInsideIndex] = this.minValue;
         }

         if (this.values[this.mousePressedInsideIndex] > this.maxValue) {
            this.values[this.mousePressedInsideIndex] = this.maxValue;
         }

         this.notifyOfChanges();
         return true;
      }
   }

   public void mouseReleased(int x, int y) {
      this.mousePressedInsideIndex = -1;
   }

   private void notifyOfChanges() {
      for(GuiControlListener<GuiSliderMultivalue> listener : this.listeners) {
         listener.valueChanged(this);
      }

   }

   public float getValue(int index) {
      return this.values[index];
   }

   public void setValue(int index, float value) {
      this.values[index] = value;
   }

   public float getMinValue() {
      return this.minValue;
   }

   public void setMinValue(float minValue) {
      this.minValue = minValue;
   }

   public float getMaxValue() {
      return this.maxValue;
   }

   public void setMaxValue(float maxValue) {
      this.maxValue = maxValue;
   }

   public void addListener(GuiControlListener<GuiSliderMultivalue> listener) {
      this.listeners.add(listener);
   }

   public void removeListener(GuiControlListener<GuiSliderMultivalue> listener) {
      this.listeners.remove(listener);
   }

   public List<GuiControlListener<GuiSliderMultivalue>> listeners() {
      return Collections.unmodifiableList(this.listeners);
   }
}
