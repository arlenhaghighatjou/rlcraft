package com.bloodnbonesgaming.lib.util.data;

import net.minecraft.advancements.critereon.MinMaxBounds;

public class MinMaxBoundsData {
   private Float min;
   private Float max;

   public MinMaxBoundsData() {
   }

   public MinMaxBounds buildBounds() {
      return this.min == null && this.max == null ? MinMaxBounds.UNBOUNDED : new MinMaxBounds(this.min, this.max);
   }

   public void setMinMax(Float min, Float max) {
      this.setMin(min);
      this.setMax(max);
   }

   public void setMin(Float min) {
      this.min = min;
   }

   public void setMax(Float max) {
      this.max = max;
   }
}
