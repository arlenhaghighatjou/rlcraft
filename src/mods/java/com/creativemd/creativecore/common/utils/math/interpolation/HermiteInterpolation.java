package com.creativemd.creativecore.common.utils.math.interpolation;

import com.creativemd.creativecore.common.utils.math.vec.Vec;

public class HermiteInterpolation<T extends Vec> extends CubicInterpolation<T> {
   public HermiteInterpolation.Tension tension;
   public double bias;

   public HermiteInterpolation(double[] times, T[] points, double bias, HermiteInterpolation.Tension tension) {
      super(times, points);
      this.bias = bias;
      this.tension = tension;
   }

   public HermiteInterpolation(double[] times, T[] points, HermiteInterpolation.Tension tension) {
      this(times, points, 0.0D, tension);
   }

   public HermiteInterpolation(double[] times, T[] points) {
      this(times, points, HermiteInterpolation.Tension.Normal);
   }

   public HermiteInterpolation(double bias, HermiteInterpolation.Tension tension, T... points) {
      super(points);
      this.bias = bias;
      this.tension = tension;
   }

   public HermiteInterpolation(HermiteInterpolation.Tension tension, T... points) {
      this(0.0D, tension, points);
   }

   public HermiteInterpolation(T... points) {
      this(HermiteInterpolation.Tension.Normal, points);
   }

   public double valueAt(double mu, int pointIndex, int pointIndexNext, int dim) {
      double v0 = this.getValue(pointIndex - 1, dim);
      double v1 = this.getValue(pointIndex, dim);
      double v2 = this.getValue(pointIndexNext, dim);
      double v3 = this.getValue(pointIndexNext + 1, dim);
      double mu2 = mu * mu;
      double mu3 = mu2 * mu;
      double m0 = (v1 - v0) * (1.0D + this.bias) * (double)(1 - this.tension.value) / 2.0D;
      m0 = m0 + (v2 - v1) * (1.0D - this.bias) * (double)(1 - this.tension.value) / 2.0D;
      double m1 = (v2 - v1) * (1.0D + this.bias) * (double)(1 - this.tension.value) / 2.0D;
      m1 = m1 + (v3 - v2) * (1.0D - this.bias) * (double)(1 - this.tension.value) / 2.0D;
      double a0 = 2.0D * mu3 - 3.0D * mu2 + 1.0D;
      double a1 = mu3 - 2.0D * mu2 + mu;
      double a2 = mu3 - mu2;
      double a3 = -2.0D * mu3 + 3.0D * mu2;
      return a0 * v1 + a1 * m0 + a2 * m1 + a3 * v2;
   }

   public static enum Tension {
      High(1),
      Normal(0),
      Low(-1);

      public final int value;

      private Tension(int value) {
         this.value = value;
      }
   }
}
