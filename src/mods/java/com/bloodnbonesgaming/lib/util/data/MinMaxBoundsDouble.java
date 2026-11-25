package com.bloodnbonesgaming.lib.util.data;

import net.minecraft.advancements.critereon.MinMaxBounds;

public class MinMaxBoundsDouble extends MinMaxBounds {
   public static final MinMaxBoundsDouble UNBOUNDED = new MinMaxBoundsDouble((Double)null, (Double)null);
   private final Double minD;
   private final Double maxD;

   public MinMaxBoundsDouble(Double min, Double max) {
      super(min != null ? min.floatValue() : null, max != null ? max.floatValue() : null);
      this.minD = min;
      this.maxD = max;
   }

   public boolean test(double value) {
      if (this.minD != null && this.minD.doubleValue() > value) {
         return false;
      } else {
         return this.maxD == null || this.maxD.doubleValue() >= value;
      }
   }
}
