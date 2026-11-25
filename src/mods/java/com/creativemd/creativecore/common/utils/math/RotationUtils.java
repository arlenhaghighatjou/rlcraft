package com.creativemd.creativecore.common.utils.math;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class RotationUtils {
   private static String[] facingNames;
   private static String[] horizontalFacingNames;
   static RotationUtils.BooleanRotation[][] rotations = new RotationUtils.BooleanRotation[3][4];

   public RotationUtils() {
   }

   public static String[] getHorizontalFacingNames() {
      if (horizontalFacingNames == null) {
         horizontalFacingNames = new String[4];

         for(int i = 0; i < horizontalFacingNames.length; ++i) {
            horizontalFacingNames[i] = EnumFacing.getHorizontal(i).getName();
         }
      }

      return horizontalFacingNames;
   }

   public static String[] getFacingNames() {
      if (facingNames == null) {
         facingNames = new String[6];

         for(int i = 0; i < facingNames.length; ++i) {
            facingNames[i] = EnumFacing.getFront(i).getName();
         }
      }

      return facingNames;
   }

   public static EnumFacing getFacing(Axis axis) {
      switch(axis) {
      case X:
         return EnumFacing.EAST;
      case Y:
         return EnumFacing.UP;
      case Z:
         return EnumFacing.NORTH;
      default:
         return null;
      }
   }

   public static Axis getThird(Axis one, Axis two) {
      switch(one) {
      case X:
         if (two == Axis.Y) {
            return Axis.Z;
         }

         return Axis.Y;
      case Y:
         if (two == Axis.X) {
            return Axis.Z;
         }

         return Axis.X;
      case Z:
         if (two == Axis.Y) {
            return Axis.X;
         }

         return Axis.Y;
      default:
         return null;
      }
   }

   public static Axis getOne(Axis axis) {
      switch(axis) {
      case X:
         return Axis.Y;
      case Y:
         return Axis.Z;
      case Z:
         return Axis.X;
      default:
         return axis;
      }
   }

   public static Axis getTwo(Axis axis) {
      switch(axis) {
      case X:
         return Axis.Z;
      case Y:
         return Axis.X;
      case Z:
         return Axis.Y;
      default:
         return axis;
      }
   }

   public static Axis rotate(Axis axis, Rotation rotation) {
      if (axis == rotation.axis) {
         return axis;
      } else {
         switch(axis) {
         case X:
            if (rotation.axis == Axis.Y) {
               return Axis.Z;
            }

            return Axis.Y;
         case Y:
            if (rotation.axis == Axis.Z) {
               return Axis.X;
            }

            return Axis.Y;
         case Z:
            if (rotation.axis == Axis.X) {
               return Axis.Y;
            }

            return Axis.X;
         default:
            return axis;
         }
      }
   }

   public static Rotation rotate(Rotation rotation, Rotation by) {
      Vector3d vec = rotation.getVec();
      by.getMatrix().transform(vec);
      return Rotation.getRotation(vec);
   }

   public static Rotation flip(Rotation rotation, Axis axis) {
      return rotation.axis == axis ? rotation.getOpposite() : rotation;
   }

   public static EnumFacing flip(EnumFacing facing, Axis axis) {
      return facing.getAxis() == axis ? facing.getOpposite() : facing;
   }

   public static EnumFacing rotate(EnumFacing facing, Rotation rotation) {
      Vec3i rotatedNormal = new Vec3i(rotation.getMatrix().getX(facing.getDirectionVec()), rotation.getMatrix().getY(facing.getDirectionVec()), rotation.getMatrix().getZ(facing.getDirectionVec()));

      for(EnumFacing rotated : EnumFacing.VALUES) {
         if (rotated.getDirectionVec().equals(rotatedNormal)) {
            return rotated;
         }
      }

      return facing;
   }

   public static Vec3i rotate(Vec3i vec, Rotation rotation) {
      return rotation.getMatrix().transform(vec);
   }

   public static BlockPos rotate(BlockPos vec, Rotation rotation) {
      return rotation.getMatrix().transform(vec);
   }

   public static void rotate(Vector3f vector, Rotation rotation) {
      rotation.getMatrix().transform(vector);
   }

   public static void rotate(Vector3d vector, Rotation rotation) {
      rotation.getMatrix().transform(vector);
   }

   public static Vec3i flip(Vec3i vec, Axis axis) {
      switch(axis) {
      case X:
         return new Vec3i(-vec.getX(), vec.getY(), vec.getZ());
      case Y:
         return new Vec3i(vec.getX(), -vec.getY(), vec.getZ());
      case Z:
         return new Vec3i(vec.getX(), vec.getY(), -vec.getZ());
      default:
         return vec;
      }
   }

   public static BlockPos flip(BlockPos vec, Axis axis) {
      switch(axis) {
      case X:
         return new BlockPos(-vec.getX(), vec.getY(), vec.getZ());
      case Y:
         return new BlockPos(vec.getX(), -vec.getY(), vec.getZ());
      case Z:
         return new BlockPos(vec.getX(), vec.getY(), -vec.getZ());
      default:
         return vec;
      }
   }

   public static void flip(Vector3f vec, Axis axis) {
      switch(axis) {
      case X:
         vec.x = -vec.x;
         break;
      case Y:
         vec.y = -vec.y;
         break;
      case Z:
         vec.z = -vec.z;
      }

   }

   public static void flip(Vector3d vec, Axis axis) {
      switch(axis) {
      case X:
         vec.x = -vec.x;
         break;
      case Y:
         vec.y = -vec.y;
         break;
      case Z:
         vec.z = -vec.z;
      }

   }

   public static boolean isFacingPositive(int index) {
      return index == 1 || index == 3 || index == 5;
   }

   public static Axis getUAxisFromFacing(EnumFacing facing) {
      switch(facing.getAxis()) {
      case X:
         return Axis.Z;
      case Y:
         return Axis.X;
      case Z:
         return Axis.X;
      default:
         return null;
      }
   }

   public static Axis getVAxisFromFacing(EnumFacing facing) {
      switch(facing.getAxis()) {
      case X:
         return Axis.Y;
      case Y:
         return Axis.Z;
      case Z:
         return Axis.Y;
      default:
         return null;
      }
   }

   public static float getUFromFacing(EnumFacing facing, float x, float y, float z) {
      switch(facing.getAxis()) {
      case X:
         return z;
      case Y:
         return x;
      case Z:
         return x;
      default:
         return 0.0F;
      }
   }

   public static float getVFromFacing(EnumFacing facing, float x, float y, float z) {
      switch(facing.getAxis()) {
      case X:
         return y;
      case Y:
         return z;
      case Z:
         return y;
      default:
         return 0.0F;
      }
   }

   public static Axis getMirrorAxis(Mirror mirrorIn) {
      switch(mirrorIn) {
      case FRONT_BACK:
         return Axis.X;
      case LEFT_RIGHT:
         return Axis.Z;
      default:
         return null;
      }
   }

   public static Rotation getRotation(net.minecraft.util.Rotation rotationIn) {
      switch(rotationIn) {
      case CLOCKWISE_90:
         return Rotation.Y_CLOCKWISE;
      case CLOCKWISE_180:
         return Rotation.Y_CLOCKWISE;
      case COUNTERCLOCKWISE_90:
         return Rotation.Y_COUNTER_CLOCKWISE;
      default:
         return null;
      }
   }

   public static int getRotationCount(net.minecraft.util.Rotation rotationIn) {
      return rotationIn == net.minecraft.util.Rotation.CLOCKWISE_180 ? 2 : 1;
   }

   public static AxisDirection getOffset(double d) {
      if (d > 0.0D) {
         return AxisDirection.POSITIVE;
      } else {
         return d < 0.0D ? AxisDirection.NEGATIVE : null;
      }
   }

   public static enum BooleanRotation {
      X_PP(Axis.X, 0, true, true),
      X_NP(Axis.X, 1, false, true),
      X_NN(Axis.X, 2, false, false),
      X_PN(Axis.X, 3, true, false),
      Y_PP(Axis.Y, 0, true, true),
      Y_PN(Axis.Y, 1, true, false),
      Y_NN(Axis.Y, 2, false, false),
      Y_NP(Axis.Y, 3, false, true),
      Z_PP(Axis.Z, 0, true, true),
      Z_NP(Axis.Z, 1, false, true),
      Z_NN(Axis.Z, 2, false, false),
      Z_PN(Axis.Z, 3, true, false);

      public final Axis axis;
      private final int index;
      private final boolean positiveOne;
      private final boolean positiveTwo;

      private BooleanRotation(Axis axis, int index, boolean positiveOne, boolean positiveTwo) {
         this.axis = axis;
         this.index = index;
         this.positiveOne = positiveOne;
         this.positiveTwo = positiveTwo;
         RotationUtils.rotations[axis.ordinal()][index] = this;
      }

      private static Axis getOne(Axis axis) {
         switch(axis) {
         case X:
            return Axis.Y;
         case Y:
         case Z:
            return Axis.X;
         default:
            return null;
         }
      }

      private static Axis getTwo(Axis axis) {
         switch(axis) {
         case X:
         case Y:
            return Axis.Z;
         case Z:
            return Axis.Y;
         default:
            return null;
         }
      }

      public RotationUtils.BooleanRotation clockwise() {
         return this.index == 3 ? RotationUtils.rotations[this.axis.ordinal()][0] : RotationUtils.rotations[this.axis.ordinal()][this.index + 1];
      }

      public RotationUtils.BooleanRotation counterClockwise() {
         return this.index == 0 ? RotationUtils.rotations[this.axis.ordinal()][3] : RotationUtils.rotations[this.axis.ordinal()][this.index - 1];
      }

      public EnumFacing clockwiseMaxFacing() {
         return this.getFacingInBetween(this.clockwise());
      }

      public EnumFacing counterMaxClockwiseFacing() {
         return this.getFacingInBetween(this.counterClockwise());
      }

      private EnumFacing getFacingInBetween(RotationUtils.BooleanRotation other) {
         if (this.positiveOne != other.positiveOne) {
            return EnumFacing.getFacingFromAxis(this.positiveTwo ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, getTwo(this.axis));
         } else if (this.positiveTwo != other.positiveTwo) {
            return EnumFacing.getFacingFromAxis(this.positiveOne ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE, getOne(this.axis));
         } else {
            throw new RuntimeException("Impossible to happen!");
         }
      }

      public boolean is(Vector3d vec) {
         return this.positiveOne == VectorUtils.get(getOne(this.axis), vec) >= 0.0D && this.positiveTwo == VectorUtils.get(getTwo(this.axis), vec) >= 0.0D;
      }

      public static RotationUtils.BooleanRotation getRotationState(Axis axis, Vector3d vec) {
         boolean positiveOne = VectorUtils.get(getOne(axis), vec) >= 0.0D;
         boolean positiveTwo = VectorUtils.get(getTwo(axis), vec) >= 0.0D;

         for(int i = 0; i < RotationUtils.rotations[axis.ordinal()].length; ++i) {
            RotationUtils.BooleanRotation rotation = RotationUtils.rotations[axis.ordinal()][i];
            if (rotation.positiveOne == positiveOne && rotation.positiveTwo == positiveTwo) {
               return rotation;
            }
         }

         return null;
      }
   }
}
