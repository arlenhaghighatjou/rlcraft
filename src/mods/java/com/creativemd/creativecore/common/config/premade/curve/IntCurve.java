package com.creativemd.creativecore.common.config.premade.curve;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.api.ICreativeConfig;

public class IntCurve implements ICreativeConfig, Curve {
   @CreativeConfig
   public int min;
   @CreativeConfig
   public int minValue;
   @CreativeConfig
   public int max;
   @CreativeConfig
   public int maxValue;

   public IntCurve(int min, int minValue, int max, int maxValue) {
      this.min = min;
      this.minValue = minValue;
      this.max = max;
      this.maxValue = maxValue;
   }

   public double valueAt(double x) {
      if (x <= (double)this.min) {
         return (double)this.minValue;
      } else if (x >= (double)this.max) {
         return (double)this.maxValue;
      } else {
         double percent = (x - (double)this.min) / (double)(this.max - this.min);
         return (double)(this.maxValue - this.minValue) * percent + (double)this.minValue;
      }
   }

   public void configured() {
      if (this.min > this.max) {
         int temp = this.min;
         this.min = this.max;
         this.max = temp;
         temp = this.minValue;
         this.minValue = this.maxValue;
         this.maxValue = temp;
      }

   }
}
