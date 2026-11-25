package com.creativemd.creativecore.common.utils.math.interpolation;

import com.creativemd.creativecore.common.utils.math.vec.Vec;

public class CosineInterpolation<T extends Vec> extends Interpolation<T> {
   public CosineInterpolation(T... points) {
      super(points);
   }

   public double valueAt(double mu, int pointIndex, int pointIndexNext, int dim) {
      double mu2 = (1.0D - Math.cos(mu * 3.141592653589793D)) / 2.0D;
      return this.getValue(pointIndex, dim) * (1.0D - mu2) + this.getValue(pointIndexNext, dim) * mu2;
   }
}
