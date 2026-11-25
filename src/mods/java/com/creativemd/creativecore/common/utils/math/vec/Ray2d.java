package com.creativemd.creativecore.common.utils.math.vec;

import com.creativemd.creativecore.common.utils.math.RotationUtils;
import javax.vecmath.Vector3d;
import net.minecraft.util.EnumFacing.Axis;

public class Ray2d {
   public double originOne;
   public double originTwo;
   public double directionOne;
   public double directionTwo;
   public Axis one;
   public Axis two;

   public Ray2d(Axis one, Axis two, Vector3d origin, double directionOne, double directionTwo) {
      this.one = one;
      this.two = two;
      this.originOne = RotationUtils.get(one, origin);
      this.originTwo = RotationUtils.get(two, origin);
      this.directionOne = directionOne;
      this.directionTwo = directionTwo;
   }

   public Ray2d(Axis one, Axis two, IVecInt origin, double directionOne, double directionTwo) {
      this.one = one;
      this.two = two;
      this.originOne = (double)origin.get(one);
      this.originTwo = (double)origin.get(two);
      this.directionOne = directionOne;
      this.directionTwo = directionTwo;
   }

   public double getOrigin(Axis axis) {
      return this.one == axis ? this.originOne : this.originTwo;
   }

   public double getDirection(Axis axis) {
      return this.one == axis ? this.directionOne : this.directionTwo;
   }

   public Axis getOther(Axis axis) {
      return this.one == axis ? this.two : this.one;
   }

   public double get(Axis axis, double value) {
      Axis other = this.getOther(axis);
      return this.getOrigin(other) + this.getDirection(other) * (value - this.getOrigin(axis)) / this.getDirection(axis);
   }

   public Double getWithLimits(Axis axis, double value) {
      return this.getWithLimits(axis, value, 0.0D, 1.0D);
   }

   public Double getWithLimits(Axis axis, double value, double min, double max) {
      Axis other = this.getOther(axis);
      double position = (value - this.getOrigin(axis)) / this.getDirection(axis);
      return position >= min && position <= max ? this.getOrigin(other) + this.getDirection(other) * position : null;
   }

   public boolean isCoordinateOnLine(int one, int two) {
      return this.get(this.one, (double)one) == (double)two;
   }

   public boolean isCoordinateToTheRight(int one, int two) {
      double tempOne = (double)one - this.originOne;
      double tempTwo = (double)two - this.originTwo;
      return this.directionOne * tempTwo - this.directionTwo * tempOne < 0.0D;
   }

   public Vector3d intersect(Ray2d line, int thirdValue) {
      if (this.directionOne * line.directionTwo - this.directionTwo * line.directionOne == 0.0D) {
         return null;
      } else {
         Vector3d vec = new Vector3d((double)thirdValue, (double)thirdValue, (double)thirdValue);
         double t = ((line.originTwo - this.originTwo) * line.directionOne + this.originOne * line.directionTwo - line.originOne * line.directionTwo) / (line.directionOne * this.directionTwo - this.directionOne * line.directionTwo);
         RotationUtils.setValue(vec, this.originOne + t * this.directionOne, this.one);
         RotationUtils.setValue(vec, this.originTwo + t * this.directionTwo, this.two);
         return vec;
      }
   }
}
