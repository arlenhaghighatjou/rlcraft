package ivorius.ivtoolkit.math;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;

public class AxisAlignedTransform2D {
   public static final AxisAlignedTransform2D R0 = new AxisAlignedTransform2D(0, false);
   public static final AxisAlignedTransform2D R1 = new AxisAlignedTransform2D(1, false);
   public static final AxisAlignedTransform2D R2 = new AxisAlignedTransform2D(2, false);
   public static final AxisAlignedTransform2D R3 = new AxisAlignedTransform2D(3, false);
   public static final AxisAlignedTransform2D R0_F = new AxisAlignedTransform2D(0, true);
   public static final AxisAlignedTransform2D R1_F = new AxisAlignedTransform2D(1, true);
   public static final AxisAlignedTransform2D R2_F = new AxisAlignedTransform2D(2, true);
   public static final AxisAlignedTransform2D R3_F = new AxisAlignedTransform2D(3, true);
   public static final AxisAlignedTransform2D ORIGINAL = R0;
   private final int rotation;
   private final boolean mirrorX;

   /** @deprecated */
   @Deprecated
   public AxisAlignedTransform2D(int rotationClockwise, boolean mirrorX) {
      this.rotation = (rotationClockwise % 4 + 4) % 4;
      this.mirrorX = mirrorX;
   }

   /** @deprecated */
   @Deprecated
   public static AxisAlignedTransform2D transform(int rotationClockwise, boolean flipX) {
      return from(rotationClockwise, flipX);
   }

   public static AxisAlignedTransform2D from(int rotationClockwise, boolean flipX) {
      switch((rotationClockwise % 4 + 4) % 4) {
      case 0:
         return flipX ? R0_F : R0;
      case 1:
         return flipX ? R1_F : R1;
      case 2:
         return flipX ? R2_F : R2;
      case 3:
         return flipX ? R3_F : R3;
      default:
         throw new InternalError();
      }
   }

   public static AxisAlignedTransform2D transform(AxisAlignedTransform2D original, int rotationClockwise, boolean flipX) {
      return flipX ? original.rotateClockwise(rotationClockwise).flipX() : original.rotateClockwise(rotationClockwise);
   }

   public int getRotation() {
      return this.rotation;
   }

   public boolean isMirrorX() {
      return this.mirrorX;
   }

   public AxisAlignedTransform2D rotateClockwise(int steps) {
      return from(this.rotation + steps, this.mirrorX);
   }

   public AxisAlignedTransform2D rotateClockwise() {
      return from(this.rotation + 1, this.mirrorX);
   }

   public AxisAlignedTransform2D rotateCounterClockwise(int steps) {
      return from(this.rotation - steps, this.mirrorX);
   }

   public AxisAlignedTransform2D rotateCounterClockwise() {
      return from(this.rotation - 1, this.mirrorX);
   }

   public AxisAlignedTransform2D flipX() {
      return from(this.rotation, !this.mirrorX);
   }

   public AxisAlignedTransform2D flipZ() {
      return from(this.rotation + 2, !this.mirrorX);
   }

   public boolean resultSwitchesXZ() {
      return this.rotation == 1 || this.rotation == 3;
   }

   public boolean resultMirrorsFormerX() {
      return this.mirrorX ^ (this.rotation == 2 || this.rotation == 3);
   }

   public boolean resultMirrorsFormerZ() {
      return this.rotation == 1 || this.rotation == 2;
   }

   public int apply(int direction) {
      if (direction >= 0 && direction <= 3) {
         boolean mirrorApplies = this.mirrorX && this.rotation % 2 == 1;
         return (direction + this.rotation + (mirrorApplies ? 2 : 0)) % 4;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public EnumFacing apply(EnumFacing facing) {
      if (facing.getAxis() == Axis.Y) {
         return facing;
      } else {
         if (this.mirrorX && facing.getAxis() == Axis.X) {
            facing = facing.getOpposite();
         }

         return EnumFacing.HORIZONTALS[(facing.getHorizontalIndex() + this.rotation) % EnumFacing.HORIZONTALS.length];
      }
   }

   public MutableBlockPos applyOn(MutableBlockPos position, int[] size) {
      return this.applyOn(position, position, size, 1);
   }

   public MutableBlockPos applyOn(MutableBlockPos position, int[] size, int centerCorrection) {
      return this.applyOn(position, position, size, centerCorrection);
   }

   public MutableBlockPos applyOn(BlockPos position, MutableBlockPos onPosition, int[] size) {
      return this.applyOn(position, onPosition, size, 1);
   }

   public MutableBlockPos applyOn(BlockPos position, MutableBlockPos onPosition, int[] size, int centerCorrection) {
      int positionX = this.mirrorX ? size[0] - centerCorrection - position.getX() : position.getX();
      switch(this.rotation) {
      case 0:
         return onPosition.setPos(positionX, position.getY(), position.getZ());
      case 1:
         return onPosition.setPos(size[2] - centerCorrection - position.getZ(), position.getY(), positionX);
      case 2:
         return onPosition.setPos(size[0] - centerCorrection - positionX, position.getY(), size[2] - centerCorrection - position.getZ());
      case 3:
         return onPosition.setPos(position.getZ(), position.getY(), size[0] - centerCorrection - positionX);
      default:
         throw new InternalError();
      }
   }

   public BlockPos apply(BlockPos position, int[] size) {
      return this.apply(position, size, 1);
   }

   public BlockPos apply(BlockPos position, int[] size, int centerCorrection) {
      int positionX = this.mirrorX ? size[0] - centerCorrection - position.getX() : position.getX();
      switch(this.rotation) {
      case 0:
         return new BlockPos(positionX, position.getY(), position.getZ());
      case 1:
         return new BlockPos(size[2] - centerCorrection - position.getZ(), position.getY(), positionX);
      case 2:
         return new BlockPos(size[0] - centerCorrection - positionX, position.getY(), size[2] - centerCorrection - position.getZ());
      case 3:
         return new BlockPos(position.getZ(), position.getY(), size[0] - centerCorrection - positionX);
      default:
         throw new InternalError();
      }
   }

   public double[] apply(double[] position, int[] size) {
      return this.applyOn(position, new double[position.length], size, 0);
   }

   public double[] applyOn(double[] position, int[] size) {
      return this.applyOn(position, position, size, 0);
   }

   public double[] applyOn(double[] position, int[] size, int centerCorrection) {
      return this.applyOn(position, position, size, centerCorrection);
   }

   public double[] applyOn(double[] position, double[] on, int[] size, int centerCorrection) {
      double positionX = this.mirrorX ? (double)(size[0] - centerCorrection) - position[0] : position[0];
      double x;
      double z;
      switch(this.rotation) {
      case 0:
         x = positionX;
         z = position[2];
         break;
      case 1:
         x = (double)(size[2] - centerCorrection) - position[2];
         z = positionX;
         break;
      case 2:
         x = (double)(size[0] - centerCorrection) - positionX;
         z = (double)(size[2] - centerCorrection) - position[2];
         break;
      case 3:
         x = position[2];
         z = (double)(size[0] - centerCorrection) - positionX;
         break;
      default:
         throw new InternalError();
      }

      on[0] = x;
      on[1] = position[1];
      on[2] = z;
      return on;
   }

   public float[] apply(float[] position, int[] size) {
      return this.applyOn(position, new float[position.length], size, 0);
   }

   public float[] applyOn(float[] position, int[] size) {
      return this.applyOn(position, position, size, 0);
   }

   public float[] applyOn(float[] position, int[] size, int centerCorrection) {
      return this.applyOn(position, position, size, centerCorrection);
   }

   public float[] applyOn(float[] position, float[] on, int[] size, int centerCorrection) {
      float positionX = this.mirrorX ? (float)(size[0] - centerCorrection) - position[0] : position[0];
      float x;
      float z;
      switch(this.rotation) {
      case 0:
         x = positionX;
         z = position[2];
         break;
      case 1:
         x = (float)(size[2] - centerCorrection) - position[2];
         z = positionX;
         break;
      case 2:
         x = (float)(size[0] - centerCorrection) - positionX;
         z = (float)(size[2] - centerCorrection) - position[2];
         break;
      case 3:
         x = position[2];
         z = (float)(size[0] - centerCorrection) - positionX;
         break;
      default:
         throw new InternalError();
      }

      on[0] = x;
      on[1] = position[1];
      on[2] = z;
      return on;
   }

   /** @deprecated */
   @Deprecated
   public int[] apply(int[] position, int[] size) {
      return this.applyOn(position, new int[position.length], size, 1);
   }

   public int[] applyOn(int[] position, int[] size) {
      return this.applyOn(position, position, size, 0);
   }

   public int[] applyOn(int[] position, int[] size, int centerCorrection) {
      return this.applyOn(position, position, size, centerCorrection);
   }

   public int[] applyOn(int[] position, int[] on, int[] size, int centerCorrection) {
      int positionX = this.mirrorX ? size[0] - centerCorrection - position[0] : position[0];
      int x;
      int z;
      switch(this.rotation) {
      case 0:
         x = positionX;
         z = position[2];
         break;
      case 1:
         x = size[2] - centerCorrection - position[2];
         z = positionX;
         break;
      case 2:
         x = size[0] - centerCorrection - positionX;
         z = size[2] - centerCorrection - position[2];
         break;
      case 3:
         x = position[2];
         z = size[0] - centerCorrection - positionX;
         break;
      default:
         throw new InternalError();
      }

      on[0] = x;
      on[1] = position[1];
      on[2] = z;
      return on;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         AxisAlignedTransform2D that = (AxisAlignedTransform2D)o;
         if (this.rotation != that.rotation) {
            return false;
         } else {
            return this.mirrorX == that.mirrorX;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.rotation;
      result = 31 * result + (this.mirrorX ? 1 : 0);
      return result;
   }

   public String toString() {
      return "AxisAlignedTransform2D{rotation=" + this.rotation + ", mirrorX=" + this.mirrorX + '}';
   }
}
