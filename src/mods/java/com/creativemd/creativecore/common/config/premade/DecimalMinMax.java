package com.creativemd.creativecore.common.config.premade;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.api.ICreativeConfig;
import java.util.Random;

public class DecimalMinMax implements ICreativeConfig {
   @CreativeConfig
   public double min;
   @CreativeConfig
   public double max;

   public DecimalMinMax(double min, double max) {
      this.min = min;
      this.max = max;
   }

   public double next(Random rand) {
      return this.min == this.max ? this.min : this.min + rand.nextDouble() * (this.max - this.min);
   }

   public void configured() {
      if (this.min > this.max) {
         double temp = this.min;
         this.min = this.max;
         this.max = temp;
      }

   }

   public double spanLength() {
      return this.max - this.min;
   }
}
