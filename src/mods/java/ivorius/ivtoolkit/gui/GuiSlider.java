package ivorius.ivtoolkit.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GuiSlider extends GuiButton {
   private float value;
   public boolean mousePressedInside;
   private List<GuiControlListener<GuiSlider>> listeners = new ArrayList();
   private float minValue = 0.0F;
   private float maxValue = 1.0F;

   public GuiSlider(int id, int x, int y, int width, int height, String displayString) {
      super(id, x, y, width, height, displayString);
   }

   public int getHoverState(boolean mouseHovering) {
      return 0;
   }

   protected void mouseDragged(Minecraft mc, int x, int y) {
      if (this.visible) {
         if (this.mousePressedInside) {
            this.value = (float)(x - (this.x + 4)) / (float)(this.width - 8);
            this.value = this.value * (this.maxValue - this.minValue) + this.minValue;
            if (this.value < this.minValue) {
               this.value = this.minValue;
            }

            if (this.value > this.maxValue) {
               this.value = this.maxValue;
            }

            this.notifyOfChanges();
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         float drawVal = (this.value - this.minValue) / (this.maxValue - this.minValue);
         this.drawTexturedModalRect(this.x + (int)(drawVal * (float)(this.width - 8)), this.y, 0, 66, 4, this.height);
         this.drawTexturedModalRect(this.x + (int)(drawVal * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, this.height);
      }

   }

   public boolean mousePressed(Minecraft mc, int x, int y) {
      if (super.mousePressed(mc, x, y)) {
         this.value = (float)(x - (this.x + 4)) / (float)(this.width - 8);
         this.value = this.value * (this.maxValue - this.minValue) + this.minValue;
         if (this.value < this.minValue) {
            this.value = this.minValue;
         }

         if (this.value > this.maxValue) {
            this.value = this.maxValue;
         }

         this.mousePressedInside = true;
         this.notifyOfChanges();
         return true;
      } else {
         return false;
      }
   }

   public void mouseReleased(int x, int y) {
      this.mousePressedInside = false;
   }

   private void notifyOfChanges() {
      for(GuiControlListener<GuiSlider> listener : this.listeners) {
         listener.valueChanged(this);
      }

   }

   public float getValue() {
      return this.value;
   }

   public void setValue(float value) {
      this.value = value;
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

   public void addListener(GuiControlListener<GuiSlider> listener) {
      this.listeners.add(listener);
   }

   public void removeListener(GuiControlListener<GuiSlider> listener) {
      this.listeners.remove(listener);
   }

   public List<GuiControlListener<GuiSlider>> listeners() {
      return Collections.unmodifiableList(this.listeners);
   }
}
