package com.creativemd.creativecore.common.utils.math.box;

import javax.annotation.Nullable;
import javax.vecmath.Vector3d;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CreativeAxisAlignedBB extends AxisAlignedBB {
   public CreativeAxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
      super(x1, y1, z1, x2, y2, z2);
   }

   public CreativeAxisAlignedBB(BlockPos pos) {
      super(pos);
   }

   public CreativeAxisAlignedBB(BlockPos pos1, BlockPos pos2) {
      super(pos1, pos2);
   }

   public boolean contains(Vector3d vec) {
      if (vec.x > this.minX && vec.x < this.maxX) {
         if (vec.y > this.minY && vec.y < this.maxY) {
            return vec.z > this.minZ && vec.z < this.maxZ;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public double calculateYOffsetStepUp(AxisAlignedBB other, AxisAlignedBB otherY, double offset) {
      return this.calculateYOffset(other, offset);
   }

   protected double getValueOfFacing(EnumFacing facing) {
      switch(facing) {
      case EAST:
         return this.maxX;
      case WEST:
         return this.minX;
      case UP:
         return this.maxY;
      case DOWN:
         return this.minY;
      case SOUTH:
         return this.maxZ;
      case NORTH:
         return this.minZ;
      default:
         return 0.0D;
      }
   }

   public Vec3d getCorner(BoxCorner corner) {
      return new Vec3d(this.getCornerX(corner), this.getCornerY(corner), this.getCornerZ(corner));
   }

   public Vector3d getCornerVector3d(BoxCorner corner) {
      return new Vector3d(this.getCornerX(corner), this.getCornerY(corner), this.getCornerZ(corner));
   }

   public double getCornerValue(BoxCorner corner, Axis axis) {
      return this.getValueOfFacing(corner.getFacing(axis));
   }

   public double getCornerX(BoxCorner corner) {
      return this.getValueOfFacing(corner.x);
   }

   public double getCornerY(BoxCorner corner) {
      return this.getValueOfFacing(corner.y);
   }

   public double getCornerZ(BoxCorner corner) {
      return this.getValueOfFacing(corner.z);
   }

   public Vec3d getSize() {
      return new Vec3d(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
   }

   public Vector3d getSize3d() {
      return new Vector3d(this.maxX - this.minX, this.maxY - this.minY, this.maxZ - this.minZ);
   }

   public double getVolume() {
      return (this.maxX - this.minX) * (this.maxY - this.minY) * (this.maxZ - this.minZ);
   }

   public double getIntersectionVolume(AxisAlignedBB other) {
      double d0 = Math.max(this.minX, other.minX);
      double d1 = Math.max(this.minY, other.minY);
      double d2 = Math.max(this.minZ, other.minZ);
      double d3 = Math.min(this.maxX, other.maxX);
      double d4 = Math.min(this.maxY, other.maxY);
      double d5 = Math.min(this.maxZ, other.maxZ);
      return d0 < d3 && d1 < d4 && d2 < d5 ? Math.abs((d3 - d0) * (d4 - d1) * (d5 - d2)) : 0.0D;
   }

   public double getSize(Axis axis) {
      switch(axis) {
      case X:
         return this.maxX - this.minX;
      case Y:
         return this.maxY - this.minY;
      case Z:
         return this.maxZ - this.minZ;
      default:
         return 0.0D;
      }
   }

   public double getMin(Axis axis) {
      switch(axis) {
      case X:
         return this.minX;
      case Y:
         return this.minY;
      case Z:
         return this.minZ;
      default:
         return 0.0D;
      }
   }

   public double getMax(Axis axis) {
      switch(axis) {
      case X:
         return this.maxX;
      case Y:
         return this.maxY;
      case Z:
         return this.maxZ;
      default:
         return 0.0D;
      }
   }

   public static double getValueOfFacing(AxisAlignedBB bb, EnumFacing facing) {
      switch(facing) {
      case EAST:
         return bb.maxX;
      case WEST:
         return bb.minX;
      case UP:
         return bb.maxY;
      case DOWN:
         return bb.minY;
      case SOUTH:
         return bb.maxZ;
      case NORTH:
         return bb.minZ;
      default:
         return 0.0D;
      }
   }

   public static double getMin(AxisAlignedBB bb, Axis axis) {
      switch(axis) {
      case X:
         return bb.minX;
      case Y:
         return bb.minY;
      case Z:
         return bb.minZ;
      default:
         return 0.0D;
      }
   }

   public static double getMax(AxisAlignedBB bb, Axis axis) {
      switch(axis) {
      case X:
         return bb.maxX;
      case Y:
         return bb.maxY;
      case Z:
         return bb.maxZ;
      default:
         return 0.0D;
      }
   }

   public static Vec3d getCorner(AxisAlignedBB bb, BoxCorner corner) {
      return new Vec3d(getCornerX(bb, corner), getCornerY(bb, corner), getCornerZ(bb, corner));
   }

   public static double getCornerValue(AxisAlignedBB bb, BoxCorner corner, Axis axis) {
      return getValueOfFacing(bb, corner.getFacing(axis));
   }

   public static double getCornerX(AxisAlignedBB bb, BoxCorner corner) {
      return getValueOfFacing(bb, corner.x);
   }

   public static double getCornerY(AxisAlignedBB bb, BoxCorner corner) {
      return getValueOfFacing(bb, corner.y);
   }

   public static double getCornerZ(AxisAlignedBB bb, BoxCorner corner) {
      return getValueOfFacing(bb, corner.z);
   }

   public static boolean isClosest(Vec3d p_186661_1_, @Nullable Vec3d p_186661_2_, Vec3d p_186661_3_) {
      return p_186661_2_ == null || p_186661_1_.squareDistanceTo(p_186661_3_) < p_186661_1_.squareDistanceTo(p_186661_2_);
   }
}
