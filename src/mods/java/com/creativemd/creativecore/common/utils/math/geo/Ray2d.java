package com.creativemd.creativecore.common.utils.math.geo;

import com.creativemd.creativecore.common.utils.math.VectorUtils;
import com.creativemd.creativecore.common.utils.math.vec.IVecInt;
import com.creativemd.creativecore.common.utils.math.vec.VectorFan;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
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
      this.originOne = VectorUtils.get(one, origin);
      this.originTwo = VectorUtils.get(two, origin);
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

   public Ray2d(Axis one, Axis two, double startOne, double startTwo, double endOne, double endTwo) {
      this.set(one, two, startOne, startTwo, endOne, endTwo);
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

   public void set(Axis one, Axis two, double startOne, double startTwo, double endOne, double endTwo) {
      this.one = one;
      this.two = two;
      this.originOne = startOne;
      this.originTwo = startTwo;
      this.directionOne = endOne - startOne;
      this.directionTwo = endTwo - startTwo;
   }

   public void set(Axis one, Axis two, Vector3f first, Vector3f second) {
      this.one = one;
      this.two = two;
      this.originOne = (double)VectorUtils.get(one, first);
      this.originTwo = (double)VectorUtils.get(two, first);
      this.directionOne = (double)VectorUtils.get(one, second) - this.originOne;
      this.directionTwo = (double)VectorUtils.get(two, second) - this.originTwo;
   }

   public double getT(Axis axis, double value) {
      return (value - this.getOrigin(axis)) / this.getDirection(axis);
   }

   public double get(Axis axis, double value) {
      Axis other = this.getOther(axis);
      return this.getOrigin(other) + this.getDirection(other) * (value - this.getOrigin(axis)) / this.getDirection(axis);
   }

   public Vector2d get(double t) {
      return new Vector2d(this.originOne + this.directionOne * t, this.originTwo + this.directionTwo * t);
   }

   public Vector2f getFloat(double t) {
      return new Vector2f((float)(this.originOne + this.directionOne * t), (float)(this.originTwo + this.directionTwo * t));
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

   public boolean isCoordinateOnLine(double one, double two) {
      if (this.directionOne == 0.0D) {
         return VectorUtils.equals(this.originOne, one);
      } else {
         return this.directionTwo == 0.0D ? VectorUtils.equals(this.originTwo, two) : VectorUtils.equals(this.get(this.one, one), two);
      }
   }

   public boolean isCoordinateToTheRight(int one, int two) {
      double tempOne = (double)one - this.originOne;
      double tempTwo = (double)two - this.originTwo;
      return this.directionOne * tempTwo - this.directionTwo * tempOne < 0.0D;
   }

   public Boolean isCoordinateToTheRight(double one, double two) {
      double tempOne = one - this.originOne;
      double tempTwo = two - this.originTwo;
      double result = this.directionOne * tempTwo - this.directionTwo * tempOne;
      return result > -9.999999747378752E-5D && result < 9.999999747378752E-5D ? null : result < 0.0D;
   }

   public Vector3f intersect(Vector3f start, Vector3f end, float thirdValue) {
      float lineOriginOne = VectorUtils.get(this.one, start);
      float lineOriginTwo = VectorUtils.get(this.two, start);
      float lineDirectionOne = VectorUtils.get(this.one, end) - VectorUtils.get(this.one, start);
      float lineDirectionTwo = VectorUtils.get(this.two, end) - VectorUtils.get(this.two, start);
      if (VectorUtils.isZero(this.directionOne * (double)lineDirectionTwo - this.directionTwo * (double)lineDirectionOne)) {
         return null;
      } else {
         Vector3f vec = new Vector3f(thirdValue, thirdValue, thirdValue);
         double t = (((double)lineOriginTwo - this.originTwo) * (double)lineDirectionOne + this.originOne * (double)lineDirectionTwo - (double)(lineOriginOne * lineDirectionTwo)) / ((double)lineDirectionOne * this.directionTwo - this.directionOne * (double)lineDirectionTwo);
         VectorUtils.set(vec, (float)(this.originOne + t * this.directionOne), this.one);
         VectorUtils.set(vec, (float)(this.originTwo + t * this.directionTwo), this.two);
         return vec;
      }
   }

   public double intersectWhen(Ray2d line) throws VectorFan.ParallelException {
      if (VectorUtils.isZero(this.directionOne * line.directionTwo - this.directionTwo * line.directionOne)) {
         if (this.isCoordinateOnLine(line.originOne, line.originTwo)) {
            throw new VectorFan.ParallelException();
         } else {
            return -1.0D;
         }
      } else {
         return ((line.originTwo - this.originTwo) * line.directionOne + this.originOne * line.directionTwo - line.originOne * line.directionTwo) / (line.directionOne * this.directionTwo - this.directionOne * line.directionTwo);
      }
   }

   public Vector3d intersect(Ray2d line, int thirdValue) {
      if (VectorUtils.isZero(this.directionOne * line.directionTwo - this.directionTwo * line.directionOne)) {
         return null;
      } else {
         Vector3d vec = new Vector3d((double)thirdValue, (double)thirdValue, (double)thirdValue);
         double t = ((line.originTwo - this.originTwo) * line.directionOne + this.originOne * line.directionTwo - line.originOne * line.directionTwo) / (line.directionOne * this.directionTwo - this.directionOne * line.directionTwo);
         VectorUtils.set(vec, this.originOne + t * this.directionOne, this.one);
         VectorUtils.set(vec, this.originTwo + t * this.directionTwo, this.two);
         return vec;
      }
   }

   public String toString() {
      return this.one + "," + this.two + ",[" + this.originOne + "," + this.originTwo + "],[" + this.directionOne + "," + this.directionTwo + "]";
   }
}
