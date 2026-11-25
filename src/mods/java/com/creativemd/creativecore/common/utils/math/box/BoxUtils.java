package com.creativemd.creativecore.common.utils.math.box;

import com.creativemd.creativecore.common.utils.math.RotationUtils;
import com.creativemd.creativecore.common.utils.math.VectorUtils;
import com.creativemd.creativecore.common.utils.math.collision.CollisionCoordinator;
import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;

public class BoxUtils {
   public BoxUtils() {
   }

   public static boolean equals(double a, double b, double deviation) {
      return a == b ? true : Math.abs(a - b) < deviation;
   }

   public static boolean greaterEquals(double a, double b, double deviation) {
      return a >= (b > 0.0D ? b - deviation : b + deviation);
   }

   public static boolean insideRect(double one, double two, double minOne, double minTwo, double maxOne, double maxTwo) {
      return one > minOne && one < maxOne && two > minTwo && two < maxTwo;
   }

   public static Vector3d[] getCorners(AxisAlignedBB box) {
      Vector3d[] corners = new Vector3d[BoxCorner.values().length];

      for(int i = 0; i < corners.length; ++i) {
         corners[i] = BoxCorner.values()[i].getVector(box);
      }

      return corners;
   }

   private static double lengthIgnoreAxis(Vector3d vec, Axis axis) {
      switch(axis) {
      case X:
         return Math.sqrt(vec.y * vec.y + vec.z * vec.z);
      case Y:
         return Math.sqrt(vec.x * vec.x + vec.z * vec.z);
      case Z:
         return Math.sqrt(vec.x * vec.x + vec.y * vec.y);
      default:
         return 0.0D;
      }
   }

   private static void includeMaxRotationInBox(BoxUtils.Box box, Vector3d vec, Axis axis, CollisionCoordinator coordinator) {
      double rotation = coordinator.getRotationDegree(axis);
      if (rotation != 0.0D) {
         Matrix3d matrix = coordinator.getRotationMatrix(axis);
         Double length = null;
         RotationUtils.BooleanRotation state = RotationUtils.BooleanRotation.getRotationState(axis, vec);
         boolean positive = rotation > 0.0D;
         int quarterRotation = 90;
         if (rotation >= 90.0D) {
            while((double)quarterRotation <= Math.abs(rotation) && quarterRotation < 360) {
               EnumFacing facing = positive ? state.clockwiseMaxFacing() : state.counterMaxClockwiseFacing();
               if (length == null) {
                  length = lengthIgnoreAxis(vec, axis);
               }

               box.include(facing, length.doubleValue());
               if (coordinator.translation != null) {
                  box.include(facing, length.doubleValue() + VectorUtils.get(facing.getAxis(), coordinator.translation));
               }

               state = state.clockwise();
               quarterRotation += 90;
            }
         }

         matrix.transform(vec);
         box.include(vec);
         if (quarterRotation <= 360 && !state.is(vec)) {
            EnumFacing facing = positive ? state.clockwiseMaxFacing() : state.counterMaxClockwiseFacing();
            if (length == null) {
               length = lengthIgnoreAxis(vec, axis);
            }

            box.include(facing, length.doubleValue());
            if (coordinator.translation != null) {
               box.include(facing, length.doubleValue() + VectorUtils.get(facing.getAxis(), coordinator.translation));
            }
         }

      }
   }

   public static AxisAlignedBB getRotatedSurrounding(AxisAlignedBB boundingBox, CollisionCoordinator coordinator) {
      Vector3d[] corners = getRotatedCorners(boundingBox, coordinator.origin);
      BoxUtils.Box bb = new BoxUtils.Box();

      for(int i = 0; i < corners.length; ++i) {
         Vector3d vec = corners[i];
         bb.include(vec);
         if (coordinator.hasOnlyTranslation()) {
            vec.add(coordinator.translation);
            bb.include(vec);
         } else {
            includeMaxRotationInBox(bb, new Vector3d(vec), Axis.X, coordinator);
            includeMaxRotationInBox(bb, new Vector3d(vec), Axis.Y, coordinator);
            includeMaxRotationInBox(bb, new Vector3d(vec), Axis.Z, coordinator);
            coordinator.transform(vec, Double.valueOf(1.0D));
            bb.include(vec);
         }
      }

      return bb.getAxisBB();
   }

   public static Vector3d[] getRotatedCorners(AxisAlignedBB box, IVecOrigin origin) {
      Vector3d[] corners = getCorners(box);

      for(int i = 0; i < corners.length; ++i) {
         Vector3d vec = corners[i];
         origin.transformPointToWorld(vec);
      }

      return corners;
   }

   public static Vector3d[] getOuterCorner(EnumFacing facing, IVecOrigin origin, AxisAlignedBB box, double minOne, double minTwo, double maxOne, double maxTwo) {
      Vector3d[] corners = getCorners(box);
      boolean positive = facing.getAxisDirection() == AxisDirection.POSITIVE;
      double value = 0.0D;
      BoxCorner selected = null;
      Axis axis = facing.getAxis();
      Axis one = RotationUtils.getOne(axis);
      Axis two = RotationUtils.getTwo(axis);

      for(int i = 0; i < corners.length; ++i) {
         Vector3d vec = corners[i];
         origin.transformPointToWorld(vec);
         double vectorValue = VectorUtils.get(axis, vec);
         if (selected != null) {
            if (positive) {
               if (vectorValue <= value) {
                  continue;
               }
            } else if (vectorValue >= value) {
               continue;
            }
         }

         selected = BoxCorner.values()[i];
         value = vectorValue;
      }

      return new Vector3d[]{corners[selected.ordinal()], corners[selected.neighborOne.ordinal()], corners[selected.neighborTwo.ordinal()], corners[selected.neighborThree.ordinal()]};
   }

   private static class Box {
      public double minX = Double.MAX_VALUE;
      public double minY = Double.MAX_VALUE;
      public double minZ = Double.MAX_VALUE;
      public double maxX = -1.7976931348623157E308D;
      public double maxY = -1.7976931348623157E308D;
      public double maxZ = -1.7976931348623157E308D;

      public Box() {
      }

      public void include(Vector3d vec) {
         this.minX = Math.min(this.minX, vec.x);
         this.minY = Math.min(this.minY, vec.y);
         this.minZ = Math.min(this.minZ, vec.z);
         this.maxX = Math.max(this.maxX, vec.x);
         this.maxY = Math.max(this.maxY, vec.y);
         this.maxZ = Math.max(this.maxZ, vec.z);
      }

      public void include(EnumFacing facing, double value) {
         switch(facing) {
         case EAST:
            this.maxX = Math.max(this.maxX, value);
            break;
         case WEST:
            this.minX = Math.min(this.minX, value);
            break;
         case UP:
            this.maxY = Math.max(this.maxY, value);
            break;
         case DOWN:
            this.minY = Math.min(this.minY, value);
            break;
         case SOUTH:
            this.maxZ = Math.max(this.maxZ, value);
            break;
         case NORTH:
            this.minZ = Math.min(this.minZ, value);
         }

      }

      public void translate(Vector3d translation) {
         this.minX += translation.x;
         this.minY += translation.y;
         this.minZ += translation.z;
         this.maxX += translation.x;
         this.maxY += translation.y;
         this.maxZ += translation.z;
      }

      public AxisAlignedBB getAxisBB() {
         return new AxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
      }
   }
}
