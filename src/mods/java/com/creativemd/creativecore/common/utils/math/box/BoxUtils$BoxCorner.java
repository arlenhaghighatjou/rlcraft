package com.creativemd.creativecore.common.utils.math.box;

import com.creativemd.creativecore.common.utils.math.Rotation;
import javax.vecmath.Vector3d;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;

public enum BoxUtils$BoxCorner {
   EUN(EnumFacing.EAST, EnumFacing.UP, EnumFacing.NORTH),
   EUS(EnumFacing.EAST, EnumFacing.UP, EnumFacing.SOUTH),
   EDN(EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.NORTH),
   EDS(EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.SOUTH),
   WUN(EnumFacing.WEST, EnumFacing.UP, EnumFacing.NORTH),
   WUS(EnumFacing.WEST, EnumFacing.UP, EnumFacing.SOUTH),
   WDN(EnumFacing.WEST, EnumFacing.DOWN, EnumFacing.NORTH),
   WDS(EnumFacing.WEST, EnumFacing.DOWN, EnumFacing.SOUTH);

   public final EnumFacing x;
   public final EnumFacing y;
   public final EnumFacing z;
   public BoxUtils$BoxCorner neighborOne;
   public BoxUtils$BoxCorner neighborTwo;
   public BoxUtils$BoxCorner neighborThree;

   private BoxUtils$BoxCorner(EnumFacing x, EnumFacing y, EnumFacing z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   private void initCorner() {
      this.neighborOne = getCorner(this.x.getOpposite(), this.y, this.z);
      this.neighborTwo = getCorner(this.x, this.y.getOpposite(), this.z);
      this.neighborThree = getCorner(this.x, this.y, this.z.getOpposite());
   }

   public Vector3d getVector(AxisAlignedBB box) {
      return new Vector3d(CreativeAxisAlignedBB.getCornerX(box, this), CreativeAxisAlignedBB.getCornerY(box, this), CreativeAxisAlignedBB.getCornerZ(box, this));
   }

   public boolean isFacingPositive(Axis axis) {
      return this.getFacing(axis).getAxisDirection() == AxisDirection.POSITIVE;
   }

   public EnumFacing getFacing(Axis axis) {
      // $FF: Couldn't be decompiled
   }

   public BoxUtils$BoxCorner flip(Axis axis) {
      // $FF: Couldn't be decompiled
   }

   public BoxUtils$BoxCorner rotate(Rotation rotation) {
      int normalX = this.x.getAxisDirection().getOffset();
      int normalY = this.y.getAxisDirection().getOffset();
      int normalZ = this.z.getAxisDirection().getOffset();
      return getCorner(EnumFacing.getFacingFromAxis(rotation.getMatrix().getX(normalX, normalY, normalZ) > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, Axis.X), EnumFacing.getFacingFromAxis(rotation.getMatrix().getY(normalX, normalY, normalZ) > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, Axis.Y), EnumFacing.getFacingFromAxis(rotation.getMatrix().getZ(normalX, normalY, normalZ) > 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, Axis.Z));
   }

   public static BoxUtils$BoxCorner getCornerUnsorted(EnumFacing facing) {
      // $FF: Couldn't be decompiled
   }

   public static BoxUtils$BoxCorner getCornerUnsorted(EnumFacing facing, EnumFacing facing2, EnumFacing facing3) {
      return getCorner(facing.getAxis() != Axis.X ? (facing2.getAxis() != Axis.X ? facing3 : facing2) : facing, facing.getAxis() != Axis.Y ? (facing2.getAxis() != Axis.Y ? facing3 : facing2) : facing, facing.getAxis() != Axis.Z ? (facing2.getAxis() != Axis.Z ? facing3 : facing2) : facing);
   }

   public static BoxUtils$BoxCorner getCorner(EnumFacing x, EnumFacing y, EnumFacing z) {
      for(BoxUtils$BoxCorner corner : values()) {
         if (corner.x == x && corner.y == y && corner.z == z) {
            return corner;
         }
      }

      return null;
   }

   private static void initCorners() {
      for(BoxUtils$BoxCorner corner : values()) {
         corner.initCorner();
      }

   }
}
