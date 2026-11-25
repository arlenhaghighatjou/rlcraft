package com.creativemd.creativecore.common.utils.math.interpolation;

import com.creativemd.creativecore.common.utils.math.vec.Vec;

public class CubicInterpolation<T extends Vec> extends Interpolation<T> {
   public T beginVec;
   public T endVec;

   public CubicInterpolation(double[] times, T[] points) {
      super(times, points);
      this.beginVec = points[0].add(points[0].sub(points[1]));
      this.endVec = points[points.length - 1].add(points[points.length - 1].sub(points[points.length - 2]));
   }

   public CubicInterpolation(T... points) {
      super(points);
      this.beginVec = points[0].add(points[0].sub(points[1]));
      this.endVec = points[points.length - 1].add(points[points.length - 1].sub(points[points.length - 2]));
   }

   protected double getValue(int index, int dim) {
      if (index < 0) {
         return this.beginVec.getValueByDim(dim);
      } else {
         return index >= this.points.size() ? this.endVec.getValueByDim(dim) : ((Vec)this.pointVecs.get(index)).getValueByDim(dim);
      }
   }

   public double valueAt(double mu, int pointIndex, int pointIndexNext, int dim) {
      double v0 = this.getValue(pointIndex - 1, dim);
      double v1 = this.getValue(pointIndex, dim);
      double v2 = this.getValue(pointIndexNext, dim);
      double v3 = this.getValue(pointIndexNext + 1, dim);
      double mu2 = mu * mu;
      double a0 = v3 - v2 - v0 + v1;
      double a1 = v0 - v1 - a0;
      double a2 = v2 - v0;
      return a0 * mu * mu2 + a1 * mu2 + a2 * mu + v1;
   }
}
