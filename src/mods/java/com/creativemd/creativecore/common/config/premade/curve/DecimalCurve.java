package com.creativemd.creativecore.common.config.premade.curve;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.api.ICreativeConfig;
import com.creativemd.creativecore.common.config.premade.DecimalMinMax;
import com.creativemd.creativecore.common.config.premade.IntMinMax;
import java.util.Random;

public class DecimalCurve implements ICreativeConfig, Curve {
   @CreativeConfig
   public double min;
   @CreativeConfig
   public double minValue;
   @CreativeConfig
   public double max;
   @CreativeConfig
   public double maxValue;

   public DecimalCurve(double min, double minValue, double max, double maxValue) {
      this.min = min;
      this.minValue = minValue;
      this.max = max;
      this.maxValue = maxValue;
   }

   public DecimalCurve(Random rand, IntMinMax duration, DecimalMinMax intensity) {
      this.min = 0.0D;
      this.minValue = intensity.next(rand);
      this.max = (double)duration.next(rand);
      this.maxValue = 0.0D;
   }

   public DecimalCurve(Random rand, IntMinMax duration, double intensity) {
      this.min = 0.0D;
      this.minValue = intensity;
      this.max = (double)duration.next(rand);
      this.maxValue = 0.0D;
   }

   public double valueAt(double x) {
      if (x <= this.min) {
         return this.minValue;
      } else if (x >= this.max) {
         return this.maxValue;
      } else {
         double percent = (x - this.min) / (this.max - this.min);
         return (this.maxValue - this.minValue) * percent + this.minValue;
      }
   }

   public void configured() {
      if (this.min > this.max) {
         double temp = this.min;
         this.min = this.max;
         this.max = temp;
         temp = this.minValue;
         this.minValue = this.maxValue;
         this.maxValue = temp;
      }

   }
}
