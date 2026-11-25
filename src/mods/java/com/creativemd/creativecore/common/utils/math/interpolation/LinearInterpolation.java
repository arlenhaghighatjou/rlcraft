package com.creativemd.creativecore.common.utils.math.interpolation;

import com.creativemd.creativecore.common.utils.math.vec.Vec;

public class LinearInterpolation<T extends Vec> extends Interpolation<T> {
   public LinearInterpolation(double[] times, T[] points) {
      super(times, points);
   }

   public LinearInterpolation(T... points) {
      super(points);
   }

   public double valueAt(double mu, int pointIndex, int pointIndexNext, int dim) {
      return (this.getValue(pointIndexNext, dim) - this.getValue(pointIndex, dim)) * mu + this.getValue(pointIndex, dim);
   }
}
