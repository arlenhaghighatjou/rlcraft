package com.creativemd.creativecore.common.config.premade;

import com.creativemd.creativecore.common.config.api.CreativeConfig;
import com.creativemd.creativecore.common.config.api.ICreativeConfig;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class IntMinMax implements ICreativeConfig {
   @CreativeConfig
   public int min;
   @CreativeConfig
   public int max;

   public IntMinMax(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public int next(EaglercraftRandom rand) {
      return this.min == this.max ? this.min : this.min + rand.nextInt(this.max - this.min);
   }

   public void configured() {
      if (this.min > this.max) {
         int temp = this.min;
         this.min = this.max;
         this.max = temp;
      }

   }

   public int spanLength() {
      return this.max - this.min;
   }
}
