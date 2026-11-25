package com.creativemd.creativecore.common.utils.math.collision;

import com.creativemd.creativecore.common.utils.math.BooleanUtils;
import com.creativemd.creativecore.common.utils.math.box.BoxCorner;
import com.creativemd.creativecore.common.utils.math.box.BoxFace;
import com.creativemd.creativecore.common.utils.math.box.BoxUtils;
import com.creativemd.creativecore.common.utils.math.box.CreativeAxisAlignedBB;
import com.creativemd.creativecore.common.utils.math.box.OrientatedBoundingBox;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

public class CollidingPlane {
   public final CreativeAxisAlignedBB bb;
   public final EnumFacing facing;
   public final CollidingPlane.PlaneCache cache;
   protected final Vector3d origin;
   protected final Vector3d normal;
   public static final int accuracySteps = 10;

   public CollidingPlane(CreativeAxisAlignedBB bb, EnumFacing facing, CollidingPlane.PlaneCache cache, Vector3d[] corners, BoxCorner[] planeCorners) {
      this.bb = bb;
      this.facing = facing;
      this.cache = cache;
      this.origin = corners[planeCorners[0].ordinal()];
      Vector3d first = corners[planeCorners[1].ordinal()];
      Vector3d second = corners[planeCorners[2].ordinal()];
      this.normal = new Vector3d((first.y - this.origin.y) * (second.z - this.origin.z) - (first.z - this.origin.z) * (second.y - this.origin.y), (first.z - this.origin.z) * (second.x - this.origin.x) - (first.x - this.origin.x) * (second.z - this.origin.z), (first.x - this.origin.x) * (second.y - this.origin.y) - (first.y - this.origin.y) * (second.x - this.origin.x));
   }

   public Boolean isInFront(Vector3d vec) {
      double scalar = (vec.x - this.origin.x) * this.normal.x + (vec.y - this.origin.y) * this.normal.y + (vec.z - this.origin.z) * this.normal.z;
      if (scalar > 0.0D) {
         return true;
      } else {
         return scalar < 0.0D ? false : null;
      }
   }

   public Double binarySearch(@Nullable Double value, AxisAlignedBB toCheck, double checkRadiusSquared, Vector3d center, CollisionCoordinator coordinator) {
      if (coordinator.isSimple) {
         Double t = this.searchBetweenSimple(value, center, new Vector3d(center), new Vector3d(), 0.0D, 1.0D, coordinator, 0);
         return t != null && this.intersects(toCheck, checkRadiusSquared, center, t.doubleValue(), coordinator) ? t : null;
      } else if (coordinator.hasOneRotation && !coordinator.hasTranslation) {
         int halfRotations = coordinator.getNumberOfHalfRotations();
         double halfRotationSize = 1.0D / (double)halfRotations;
         Vector3d temp = new Vector3d();
         Vector3d start = new Vector3d(center);
         Double t = this.searchBetweenSimple(value, center, start, temp, 0.0D, halfRotationSize, coordinator, 0);
         if (t != null && this.intersects(toCheck, checkRadiusSquared, center, t.doubleValue(), coordinator)) {
            return t;
         } else {
            start.set(center);
            coordinator.transformInverted(start, Double.valueOf(halfRotationSize));
            t = this.searchBetweenSimple(value, center, new Vector3d(center), temp, halfRotationSize, halfRotationSize * 2.0D, coordinator, 0);
            return t != null && this.intersects(toCheck, checkRadiusSquared, center, t.doubleValue(), coordinator) ? t : null;
         }
      } else {
         Vector3d start = new Vector3d(center);
         Vector3d temp = new Vector3d();
         int halfRotations = coordinator.getNumberOfHalfRotations();
         double halfRotationSize = 1.0D / (double)halfRotations;

         for(int i = 0; i < halfRotations; ++i) {
            double startT = halfRotationSize * (double)i;
            double endT = halfRotationSize * (double)(i + 1);
            if (startT != 0.0D) {
               start.set(center);
               coordinator.transformInverted(start, Double.valueOf(startT));
            }

            if (value != null && value.doubleValue() <= startT) {
               return null;
            }

            Double t = this.searchBetweenSimple(value, center, start, temp, startT, endT, coordinator, 0);
            if (t != null && this.intersects(toCheck, checkRadiusSquared, center, t.doubleValue(), coordinator)) {
               return t;
            }
         }

         return null;
      }
   }

   protected Double searchBetweenSimple(@Nullable Double value, Vector3d center, Vector3d start, Vector3d temp, double startT, double endT, CollisionCoordinator coordinator, int steps) {
      if (value != null && value.doubleValue() < startT) {
         return null;
      } else {
         Boolean beforeFront = this.isInFront(start);
         if (beforeFront == null) {
            return startT;
         } else {
            temp.set(center);
            coordinator.transformInverted(temp, Double.valueOf(endT));
            Boolean afterFront = this.isInFront(temp);
            if (afterFront == null) {
               return value != null ? Math.min(value.doubleValue(), endT) : endT;
            } else if (beforeFront != afterFront) {
               if (steps < 10) {
                  ++steps;
                  double halfT = (startT + endT) / 2.0D;
                  temp.set(center);
                  coordinator.transformInverted(temp, Double.valueOf(halfT));
                  Boolean halfFront = this.isInFront(temp);
                  if (halfFront == null) {
                     return value != null ? Math.min(value.doubleValue(), halfT) : halfT;
                  } else {
                     return beforeFront != halfFront ? this.searchBetweenSimple(value, center, start, temp, startT, halfT, coordinator, steps) : this.searchBetweenSimple(value, center, temp, start, halfT, endT, coordinator, steps);
                  }
               } else {
                  return startT;
               }
            } else {
               return null;
            }
         }
      }
   }

   public boolean intersects(AxisAlignedBB toCheck, double checkRadiusSquared, Vector3d center, double t, CollisionCoordinator coordinator) {
      Vector3d cachedCenter = new Vector3d(this.cache.center);
      coordinator.origin.transformPointToWorld(cachedCenter);
      coordinator.transform(cachedCenter, Double.valueOf(t));
      cachedCenter.sub(center);
      if (cachedCenter.lengthSquared() >= checkRadiusSquared + this.cache.radiusSquared) {
         return false;
      } else {
         Matrix4d matrix = coordinator.getInverted(Double.valueOf(t));
         double minX = Double.MAX_VALUE;
         double minY = Double.MAX_VALUE;
         double minZ = Double.MAX_VALUE;
         double maxX = -1.7976931348623157E308D;
         double maxY = -1.7976931348623157E308D;
         double maxZ = -1.7976931348623157E308D;

         for(int i = 0; i < BoxCorner.values().length; ++i) {
            Vector3d corner = BoxCorner.values()[i].getVector(toCheck);
            coordinator.transform(matrix, corner);
            coordinator.origin.transformPointToFakeWorld(corner);
            if (this.bb.contains(corner)) {
               return true;
            }

            minX = Math.min(minX, corner.x);
            minY = Math.min(minY, corner.y);
            minZ = Math.min(minZ, corner.z);
            maxX = Math.max(maxX, corner.x);
            maxY = Math.max(maxY, corner.y);
            maxZ = Math.max(maxZ, corner.z);
         }

         return this.bb.minX < maxX && this.bb.maxX > minX && this.bb.minY < maxY && this.bb.maxY > minY && this.bb.minZ < maxZ && this.bb.maxZ > minZ;
      }
   }

   public static CollidingPlane[] getPlanes(CreativeAxisAlignedBB box, CollidingPlane.PlaneCache cache, CollisionCoordinator coordinator) {
      Vector3d[] corners = BoxUtils.getRotatedCorners(box, coordinator.origin);
      boolean east = coordinator.offX > 0.0D;
      boolean west = coordinator.offY < 0.0D;
      boolean up = coordinator.offY > 0.0D;
      boolean down = coordinator.offY < 0.0D;
      boolean south = coordinator.offZ > 0.0D;
      boolean north = coordinator.offZ < 0.0D;
      if (coordinator.hasRotY || coordinator.hasRotZ) {
         west = true;
         east = true;
      }

      if (coordinator.hasRotX || coordinator.hasRotZ) {
         down = true;
         up = true;
      }

      if (coordinator.hasRotX || coordinator.hasRotY) {
         north = true;
         south = true;
      }

      CollidingPlane[] planes = new CollidingPlane[BooleanUtils.countTrue(east, west, up, down, south, north)];
      int index = 0;
      if (east) {
         planes[index] = new CollidingPlane(box, EnumFacing.EAST, cache, corners, BoxFace.get(EnumFacing.EAST).corners);
         ++index;
      }

      if (west) {
         planes[index] = new CollidingPlane(box, EnumFacing.WEST, cache, corners, BoxFace.get(EnumFacing.WEST).corners);
         ++index;
      }

      if (up) {
         planes[index] = new CollidingPlane(box, EnumFacing.UP, cache, corners, BoxFace.get(EnumFacing.UP).corners);
         ++index;
      }

      if (down) {
         planes[index] = new CollidingPlane(box, EnumFacing.DOWN, cache, corners, BoxFace.get(EnumFacing.DOWN).corners);
         ++index;
      }

      if (south) {
         planes[index] = new CollidingPlane(box, EnumFacing.SOUTH, cache, corners, BoxFace.get(EnumFacing.SOUTH).corners);
         ++index;
      }

      if (north) {
         planes[index] = new CollidingPlane(box, EnumFacing.NORTH, cache, corners, BoxFace.get(EnumFacing.NORTH).corners);
         ++index;
      }

      return planes;
   }

   public static EnumFacing getDirection(CollisionCoordinator coordinator, OrientatedBoundingBox box, Vector3d center) {
      double x = (center.x - box.cache.center.x) / (box.maxX - box.minX);
      double y = (center.y - box.cache.center.y) / (box.maxY - box.minY);
      double z = (center.z - box.cache.center.z) / (box.maxZ - box.minZ);
      boolean xy = Math.abs(x) > Math.abs(y);
      boolean xz = Math.abs(x) > Math.abs(z);
      boolean yz = Math.abs(y) > Math.abs(z);
      if (xy && xz) {
         return x > 0.0D ? EnumFacing.EAST : EnumFacing.WEST;
      } else if (!xz && !yz) {
         return z > 0.0D ? EnumFacing.SOUTH : EnumFacing.NORTH;
      } else {
         return y > 0.0D ? EnumFacing.UP : EnumFacing.DOWN;
      }
   }

   public static class PlaneCache {
      public CollidingPlane[] planes;
      public final Vector3d center;
      public final double radiusSquared;

      public PlaneCache(AxisAlignedBB box) {
         this.radiusSquared = (box.minX * box.maxX + box.minY * box.maxY + box.minZ * box.maxZ) * 0.5D;
         this.center = new Vector3d(box.minX + (box.maxX - box.minX) * 0.5D, box.minY + (box.maxY - box.minY) * 0.5D, box.minZ + (box.maxZ - box.minZ) * 0.5D);
      }

      public boolean isCached() {
         return this.planes != null;
      }

      public void reset() {
         this.planes = null;
      }
   }

   public static class PushCache {
      public EnumFacing facing;
      public OrientatedBoundingBox pushBox;
      public AxisAlignedBB entityBox;
      public OrientatedBoundingBox entityBoxOrientated;

      public PushCache() {
      }
   }
}
