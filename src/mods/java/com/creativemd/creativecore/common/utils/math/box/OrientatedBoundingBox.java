package com.creativemd.creativecore.common.utils.math.box;

import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.creativecore.common.utils.math.VectorUtils;
import com.creativemd.creativecore.common.utils.math.collision.CollidingPlane;
import com.creativemd.creativecore.common.utils.math.collision.IntersectionHelper;
import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;
import java.util.List;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class OrientatedBoundingBox extends CreativeAxisAlignedBB {
   public IVecOrigin origin;
   public CollidingPlane.PlaneCache cache;
   public static final float EPSILON = 0.001F;

   public void buildCache() {
      this.cache = new CollidingPlane.PlaneCache(this);
   }

   public OrientatedBoundingBox(IVecOrigin origin, double x1, double y1, double z1, double x2, double y2, double z2) {
      super(x1, y1, z1, x2, y2, z2);
      this.origin = origin;
   }

   public OrientatedBoundingBox(IVecOrigin origin, AxisAlignedBB bb) {
      super(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
      this.origin = origin;
   }

   public OrientatedBoundingBox setMaxY(double y2) {
      return new OrientatedBoundingBox(this.origin, this.minX, this.minY, this.minZ, this.maxX, y2, this.maxZ);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof OrientatedBoundingBox)) {
         return false;
      } else {
         OrientatedBoundingBox axisalignedbb = (OrientatedBoundingBox)p_equals_1_;
         if (axisalignedbb.origin != this.origin) {
            return false;
         } else if (Double.compare(axisalignedbb.minX, this.minX) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.minY, this.minY) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.minZ, this.minZ) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.maxX, this.maxX) != 0) {
            return false;
         } else if (Double.compare(axisalignedbb.maxY, this.maxY) != 0) {
            return false;
         } else {
            return Double.compare(axisalignedbb.maxZ, this.maxZ) == 0;
         }
      }
   }

   public OrientatedBoundingBox contract(double x, double y, double z) {
      double d0 = this.minX;
      double d1 = this.minY;
      double d2 = this.minZ;
      double d3 = this.maxX;
      double d4 = this.maxY;
      double d5 = this.maxZ;
      if (x < 0.0D) {
         d0 -= x;
      } else if (x > 0.0D) {
         d3 -= x;
      }

      if (y < 0.0D) {
         d1 -= y;
      } else if (y > 0.0D) {
         d4 -= y;
      }

      if (z < 0.0D) {
         d2 -= z;
      } else if (z > 0.0D) {
         d5 -= z;
      }

      return new OrientatedBoundingBox(this.origin, d0, d1, d2, d3, d4, d5);
   }

   public OrientatedBoundingBox expand(double x, double y, double z) {
      double d0 = this.minX;
      double d1 = this.minY;
      double d2 = this.minZ;
      double d3 = this.maxX;
      double d4 = this.maxY;
      double d5 = this.maxZ;
      if (x < 0.0D) {
         d0 += x;
      } else if (x > 0.0D) {
         d3 += x;
      }

      if (y < 0.0D) {
         d1 += y;
      } else if (y > 0.0D) {
         d4 += y;
      }

      if (z < 0.0D) {
         d2 += z;
      } else if (z > 0.0D) {
         d5 += z;
      }

      return new OrientatedBoundingBox(this.origin, d0, d1, d2, d3, d4, d5);
   }

   public OrientatedBoundingBox grow(double x, double y, double z) {
      double d0 = this.minX - x;
      double d1 = this.minY - y;
      double d2 = this.minZ - z;
      double d3 = this.maxX + x;
      double d4 = this.maxY + y;
      double d5 = this.maxZ + z;
      return new OrientatedBoundingBox(this.origin, d0, d1, d2, d3, d4, d5);
   }

   public OrientatedBoundingBox grow(double value) {
      return this.grow(value, value, value);
   }

   public AxisAlignedBB intersect(AxisAlignedBB other) {
      if (other instanceof OrientatedBoundingBox) {
         OrientatedBoundingBox otherBB = (OrientatedBoundingBox)other;
         if (otherBB.origin == this.origin) {
            double d0 = Math.max(this.minX, other.minX);
            double d1 = Math.max(this.minY, other.minY);
            double d2 = Math.max(this.minZ, other.minZ);
            double d3 = Math.min(this.maxX, other.maxX);
            double d4 = Math.min(this.maxY, other.maxY);
            double d5 = Math.min(this.maxZ, other.maxZ);
            return new OrientatedBoundingBox(this.origin, d0, d1, d2, d3, d4, d5);
         } else {
            OrientatedBoundingBox converted = otherBB.origin.getOrientatedBox(this.origin.getAxisAlignedBox(this));
            double d0 = Math.max(converted.minX, other.minX);
            double d1 = Math.max(converted.minY, other.minY);
            double d2 = Math.max(converted.minZ, other.minZ);
            double d3 = Math.min(converted.maxX, other.maxX);
            double d4 = Math.min(converted.maxY, other.maxY);
            double d5 = Math.min(converted.maxZ, other.maxZ);
            return new OrientatedBoundingBox(otherBB.origin, d0, d1, d2, d3, d4, d5);
         }
      } else {
         AxisAlignedBB box = this.origin.getAxisAlignedBox(this);
         double d0 = Math.max(box.minX, other.minX);
         double d1 = Math.max(box.minY, other.minY);
         double d2 = Math.max(box.minZ, other.minZ);
         double d3 = Math.min(box.maxX, other.maxX);
         double d4 = Math.min(box.maxY, other.maxY);
         double d5 = Math.min(box.maxZ, other.maxZ);
         return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
      }
   }

   public AxisAlignedBB union(AxisAlignedBB other) {
      if (other instanceof OrientatedBoundingBox) {
         OrientatedBoundingBox otherBB = (OrientatedBoundingBox)other;
         if (otherBB.origin == this.origin) {
            double d0 = Math.min(this.minX, other.minX);
            double d1 = Math.min(this.minY, other.minY);
            double d2 = Math.min(this.minZ, other.minZ);
            double d3 = Math.max(this.maxX, other.maxX);
            double d4 = Math.max(this.maxY, other.maxY);
            double d5 = Math.max(this.maxZ, other.maxZ);
            return new OrientatedBoundingBox(this.origin, d0, d1, d2, d3, d4, d5);
         } else {
            OrientatedBoundingBox converted = otherBB.origin.getOrientatedBox(this.origin.getAxisAlignedBox(this));
            double d0 = Math.min(converted.minX, other.minX);
            double d1 = Math.min(converted.minY, other.minY);
            double d2 = Math.min(converted.minZ, other.minZ);
            double d3 = Math.max(converted.maxX, other.maxX);
            double d4 = Math.max(converted.maxY, other.maxY);
            double d5 = Math.max(converted.maxZ, other.maxZ);
            return new OrientatedBoundingBox(otherBB.origin, d0, d1, d2, d3, d4, d5);
         }
      } else {
         AxisAlignedBB box = this.origin.getAxisAlignedBox(this);
         double d0 = Math.min(box.minX, other.minX);
         double d1 = Math.min(box.minY, other.minY);
         double d2 = Math.min(box.minZ, other.minZ);
         double d3 = Math.max(box.maxX, other.maxX);
         double d4 = Math.max(box.maxY, other.maxY);
         double d5 = Math.max(box.maxZ, other.maxZ);
         return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
      }
   }

   public OrientatedBoundingBox offset(double x, double y, double z) {
      return new OrientatedBoundingBox(this.origin, this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
   }

   public OrientatedBoundingBox offset(BlockPos pos) {
      return new OrientatedBoundingBox(this.origin, this.minX + (double)pos.getX(), this.minY + (double)pos.getY(), this.minZ + (double)pos.getZ(), this.maxX + (double)pos.getX(), this.maxY + (double)pos.getY(), this.maxZ + (double)pos.getZ());
   }

   public OrientatedBoundingBox offset(Vec3d vec) {
      return this.offset(vec.x, vec.y, vec.z);
   }

   private static int getCornerOffset(double value, double min, double max) {
      if (value <= min) {
         return -1;
      } else {
         return value >= max ? 1 : 0;
      }
   }

   public double calculateDistanceRotated(AxisAlignedBB other, Axis axis, double offset) {
      boolean positive = offset > 0.0D;
      EnumFacing facing = EnumFacing.getFacingFromAxis(!positive ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, axis);
      double closestValue = getValueOfFacing(other, facing.getOpposite());
      Axis one = RotationUtils.getOne(axis);
      Axis two = RotationUtils.getTwo(axis);
      double minOne = getMin(other, one);
      double minTwo = getMin(other, two);
      double maxOne = getMax(other, one);
      double maxTwo = getMax(other, two);
      Vector3d[] corners = BoxUtils.getOuterCorner(facing, this.origin, this, minOne, minTwo, maxOne, maxTwo);
      Vector3d outerCorner = corners[0];
      double outerCornerOne = VectorUtils.get(one, outerCorner);
      double outerCornerTwo = VectorUtils.get(two, outerCorner);
      double outerCornerAxis = VectorUtils.get(axis, outerCorner);
      int outerCornerOffsetOne = getCornerOffset(outerCornerOne, minOne, maxOne);
      int outerCornerOffsetTwo = getCornerOffset(outerCornerTwo, minTwo, maxTwo);
      if (outerCornerOffsetOne == 0 && outerCornerOffsetTwo == 0) {
         return positive ? VectorUtils.get(axis, outerCorner) - closestValue : closestValue - VectorUtils.get(axis, outerCorner);
      } else {
         Vector2d[] directions = new Vector2d[3];
         double minDistance = Double.MAX_VALUE;
         Vector2d[] vectors = new Vector2d[]{new Vector2d(minOne - outerCornerOne, minTwo - outerCornerTwo), new Vector2d(maxOne - outerCornerOne, minTwo - outerCornerTwo), new Vector2d(maxOne - outerCornerOne, maxTwo - outerCornerTwo), new Vector2d(minOne - outerCornerOne, maxTwo - outerCornerTwo)};
         Vector2d[] vectorsRelative = new Vector2d[]{new Vector2d(), new Vector2d(), new Vector2d(), new Vector2d()};
         directions[0] = new Vector2d(VectorUtils.get(one, corners[1]) - outerCornerOne, VectorUtils.get(two, corners[1]) - outerCornerTwo);
         directions[1] = new Vector2d(VectorUtils.get(one, corners[2]) - outerCornerOne, VectorUtils.get(two, corners[2]) - outerCornerTwo);
         directions[2] = new Vector2d(VectorUtils.get(one, corners[3]) - outerCornerOne, VectorUtils.get(two, corners[3]) - outerCornerTwo);

         label100:
         for(int i = 0; i < 3; ++i) {
            int indexFirst = i;
            int indexSecond = i == 2 ? 0 : i + 1;
            Vector2d first = directions[i];
            Vector2d second = directions[indexSecond];
            if (first.x == 0.0D || second.y == 0.0D) {
               indexFirst = indexSecond;
               indexSecond = i;
               first = directions[indexFirst];
               second = directions[i];
            }

            double firstAxisValue = VectorUtils.get(axis, corners[indexFirst + 1]);
            double secondAxisValue = VectorUtils.get(axis, corners[indexSecond + 1]);
            boolean allInside = true;

            for(int j = 0; j < 4; ++j) {
               Vector2d vector = vectors[j];
               double t = (vector.x * second.y - vector.y * second.x) / (first.x * second.y - first.y * second.x);
               if (Double.isNaN(t) || Double.isInfinite(t)) {
                  continue label100;
               }

               double s = (vector.y - t * first.y) / second.y;
               if (Double.isNaN(s) || Double.isInfinite(s)) {
                  continue label100;
               }

               if (t <= 0.0D || t >= 1.0D || s <= 0.0D || s >= 1.0D) {
                  allInside = false;
               }

               vectorsRelative[j].set(t, s);
            }

            if (allInside) {
               for(int j = 0; j < vectorsRelative.length; ++j) {
                  double distance = calculateDistanceFromPlane(positive, closestValue, vectorsRelative[j], firstAxisValue, secondAxisValue, outerCornerAxis);
                  minDistance = Math.min(distance, minDistance);
               }
            } else {
               List<Vector2d> points = IntersectionHelper.getIntersectionShape(vectorsRelative);

               for(int j = 0; j < points.size(); ++j) {
                  double distance = calculateDistanceFromPlane(positive, closestValue, (Vector2d)points.get(j), firstAxisValue, secondAxisValue, outerCornerAxis);
                  minDistance = Math.min(distance, minDistance);
               }
            }
         }

         if (minDistance == Double.MAX_VALUE) {
            return -1.0D;
         } else {
            return minDistance;
         }
      }
   }

   public static double calculateDistanceFromPlane(boolean positive, double closestValue, Vector2d vec, double firstAxisValue, double secondAxisValue, double outerCornerAxis) {
      double valueAxis = outerCornerAxis + (firstAxisValue - outerCornerAxis) * vec.x + (secondAxisValue - outerCornerAxis) * vec.y;
      return positive ? valueAxis - closestValue : closestValue - valueAxis;
   }

   public double calculateOffsetRotated(AxisAlignedBB other, Axis axis, double offset) {
      if (offset == 0.0D) {
         return offset;
      } else {
         double distance = this.calculateDistanceRotated(other, axis, offset);
         if (distance < 0.0D && !equals(distance, 0.0D)) {
            return offset;
         } else if (offset > 0.0D) {
            return distance < offset ? distance : offset;
         } else if (offset < 0.0D) {
            return -distance > offset ? -distance : offset;
         } else {
            return offset;
         }
      }
   }

   public static boolean smallerThanAndEquals(double a, double b) {
      return a < b || equals(a, b);
   }

   public static boolean greaterThanAndEquals(double a, double b) {
      return a > b || equals(a, b);
   }

   public static boolean equals(double a, double b) {
      return a == b ? true : Math.abs(a - b) < 0.0010000000474974513D;
   }

   public double calculateYOffsetStepUp(AxisAlignedBB other, AxisAlignedBB otherY, double offset) {
      double newOffset = this.calculateYOffset(otherY, offset);
      if (offset > 0.0D) {
         if (newOffset < offset) {
            return newOffset / 2.0D;
         }
      } else if (newOffset > offset) {
         return newOffset / 2.0D;
      }

      return newOffset;
   }

   public double getMaxTranslated(Axis axis) {
      return this.getMax(axis) + this.origin.translationCombined(axis);
   }

   public double getMinTranslated(Axis axis) {
      return this.getMin(axis) + this.origin.translationCombined(axis);
   }

   public double calculateOffset(AxisAlignedBB other, Axis axis, double offset) {
      if (other instanceof OrientatedBoundingBox) {
         if (((OrientatedBoundingBox)other).origin == this.origin) {
            switch(axis) {
            case X:
               return super.calculateXOffset(other, offset);
            case Y:
               return super.calculateYOffset(other, offset);
            case Z:
               return super.calculateZOffset(other, offset);
            }
         }

         other = ((OrientatedBoundingBox)other).origin.getAxisAlignedBox(other);
      }

      if (this.origin.isRotated()) {
         return this.calculateOffsetRotated(other, axis, offset);
      } else {
         Axis one = RotationUtils.getOne(axis);
         Axis two = RotationUtils.getTwo(axis);
         if (getMax(other, one) > this.getMinTranslated(one) && getMin(other, one) < this.getMaxTranslated(one) && getMax(other, two) > this.getMinTranslated(two) && getMin(other, two) < this.getMaxTranslated(two)) {
            if (offset > 0.0D && getMax(other, axis) <= this.getMinTranslated(axis)) {
               double d1 = this.getMinTranslated(axis) - getMax(other, axis);
               if (d1 < offset) {
                  offset = d1;
               }
            } else if (offset < 0.0D && getMin(other, axis) >= this.getMaxTranslated(axis)) {
               double d0 = this.getMaxTranslated(axis) - getMin(other, axis);
               if (d0 > offset) {
                  offset = d0;
               }
            }

            return offset;
         } else {
            return offset;
         }
      }
   }

   public double calculateXOffset(AxisAlignedBB other, double offsetX) {
      return this.calculateOffset(other, Axis.X, offsetX);
   }

   public double calculateYOffset(AxisAlignedBB other, double offsetY) {
      return this.calculateOffset(other, Axis.Y, offsetY);
   }

   public double calculateZOffset(AxisAlignedBB other, double offsetZ) {
      return this.calculateOffset(other, Axis.Z, offsetZ);
   }

   public boolean intersects(AxisAlignedBB other) {
      if (!(other instanceof OrientatedBoundingBox)) {
         return this.intersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
      } else if (((OrientatedBoundingBox)other).origin == this.origin) {
         return this.minX < other.maxX && this.maxX > other.minX && this.minY < other.maxY && this.maxY > other.minY && this.minZ < other.maxZ && this.maxZ > other.minZ;
      } else {
         OrientatedBoundingBox converted = ((OrientatedBoundingBox)other).origin.getOrientatedBox(this.origin.getAxisAlignedBox(this));
         return converted.minX < other.maxX && converted.maxX > other.minX && converted.minY < other.maxY && converted.maxY > other.minY && converted.minZ < other.maxZ && converted.maxZ > other.minZ;
      }
   }

   public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      AxisAlignedBB box = this.origin.getAxisAlignedBox(this);
      return box.minX < maxX && box.maxX > minX && box.minY < maxY && box.maxY > minY && box.minZ < maxZ && box.maxZ > minZ;
   }

   public String toString() {
      return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }

   public Vector3d getCenter3d() {
      return new Vector3d(this.minX + (this.maxX - this.minX) * 0.5D, this.minY + (this.maxY - this.minY) * 0.5D, this.minZ + (this.maxZ - this.minZ) * 0.5D);
   }

   public double getPushOutScale(double minScale, OrientatedBoundingBox fakeBox, Vector3d pushVec) {
      double scale = Double.MAX_VALUE;
      boolean pushX = pushVec.x != 0.0D;
      boolean pushY = pushVec.y != 0.0D;
      boolean pushZ = pushVec.z != 0.0D;
      if (pushX) {
         if (pushVec.x > 0.0D) {
            scale = Math.min(scale, Math.abs((this.maxX - fakeBox.minX) / pushVec.x));
         } else {
            scale = Math.min(scale, Math.abs((this.minX - fakeBox.maxX) / pushVec.x));
         }
      }

      if (pushY) {
         if (pushVec.y > 0.0D) {
            scale = Math.min(scale, Math.abs((this.maxY - fakeBox.minY) / pushVec.y));
         } else {
            scale = Math.min(scale, Math.abs((this.minY - fakeBox.maxY) / pushVec.y));
         }
      }

      if (pushZ) {
         if (pushVec.z > 0.0D) {
            scale = Math.min(scale, Math.abs((this.maxZ - fakeBox.minZ) / pushVec.z));
         } else {
            scale = Math.min(scale, Math.abs((this.minZ - fakeBox.maxZ) / pushVec.z));
         }
      }

      return scale <= minScale ? minScale : scale;
   }
}
