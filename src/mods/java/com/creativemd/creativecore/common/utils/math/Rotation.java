package com.creativemd.creativecore.common.utils.math;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public enum Rotation {
   X_CLOCKWISE(Axis.X, new Rotation.RotationMatrix(1, 0, 0, 0, 0, -1, 0, 1, 0), true) {
      public Rotation getOpposite() {
         return X_COUNTER_CLOCKWISE;
      }
   },
   X_COUNTER_CLOCKWISE(Axis.X, new Rotation.RotationMatrix(1, 0, 0, 0, 0, 1, 0, -1, 0), false) {
      public Rotation getOpposite() {
         return X_CLOCKWISE;
      }
   },
   Y_CLOCKWISE(Axis.Y, new Rotation.RotationMatrix(0, 0, 1, 0, 1, 0, -1, 0, 0), true) {
      public Rotation getOpposite() {
         return Rotation.Y_COUNTER_CLOCKWISE;
      }
   },
   Y_COUNTER_CLOCKWISE(Axis.Y, new Rotation.RotationMatrix(0, 0, -1, 0, 1, 0, 1, 0, 0), false) {
      public Rotation getOpposite() {
         return Rotation.Y_CLOCKWISE;
      }
   },
   Z_CLOCKWISE(Axis.Z, new Rotation.RotationMatrix(0, -1, 0, 1, 0, 0, 0, 0, 1), true) {
      public Rotation getOpposite() {
         return Rotation.Z_COUNTER_CLOCKWISE;
      }
   },
   Z_COUNTER_CLOCKWISE(Axis.Z, new Rotation.RotationMatrix(0, 1, 0, -1, 0, 0, 0, 0, 1), false) {
      public Rotation getOpposite() {
         return Z_CLOCKWISE;
      }
   };

   public final Axis axis;
   public final int direction;
   public final boolean clockwise;
   private final Vector3d vec;
   private final Rotation.RotationMatrix rotationMatrix;

   public static Rotation getRotation(Axis axis, boolean clockwise) {
      switch(axis) {
      case X:
         return clockwise ? X_CLOCKWISE : X_COUNTER_CLOCKWISE;
      case Y:
         return clockwise ? Y_CLOCKWISE : Y_COUNTER_CLOCKWISE;
      case Z:
         return clockwise ? Z_CLOCKWISE : Z_COUNTER_CLOCKWISE;
      default:
         return null;
      }
   }

   public static Rotation getRotation(Vector3d vec) {
      if (vec.x > 0.0D) {
         return X_CLOCKWISE;
      } else if (vec.x < 0.0D) {
         return X_COUNTER_CLOCKWISE;
      } else if (vec.y > 0.0D) {
         return Y_CLOCKWISE;
      } else if (vec.y < 0.0D) {
         return Y_COUNTER_CLOCKWISE;
      } else if (vec.z > 0.0D) {
         return Z_CLOCKWISE;
      } else {
         return vec.z < 0.0D ? Z_COUNTER_CLOCKWISE : null;
      }
   }

   private Rotation(Axis axis, Rotation.RotationMatrix matrix, boolean clockwise) {
      this.rotationMatrix = matrix;
      this.axis = axis;
      this.clockwise = clockwise;
      this.direction = clockwise ? 1 : -1;
      this.vec = new Vector3d();
      VectorUtils.set(this.vec, (double)this.direction, axis);
   }

   public boolean getRotatedComponentPositive(Axis axis) {
      switch(axis) {
      case X:
         if (this.rotationMatrix.m00 != 0) {
            return this.rotationMatrix.m00 > 0;
         } else {
            if (this.rotationMatrix.m10 != 0) {
               return this.rotationMatrix.m10 > 0;
            }

            return this.rotationMatrix.m20 > 0;
         }
      case Y:
         if (this.rotationMatrix.m01 != 0) {
            return this.rotationMatrix.m01 > 0;
         } else {
            if (this.rotationMatrix.m11 != 0) {
               return this.rotationMatrix.m11 > 0;
            }

            return this.rotationMatrix.m21 > 0;
         }
      case Z:
         if (this.rotationMatrix.m02 != 0) {
            return this.rotationMatrix.m02 > 0;
         } else {
            if (this.rotationMatrix.m12 != 0) {
               return this.rotationMatrix.m12 > 0;
            }

            return this.rotationMatrix.m22 > 0;
         }
      default:
         return true;
      }
   }

   public Axis getRotatedComponent(Axis axis) {
      switch(axis) {
      case X:
         if (this.rotationMatrix.m00 != 0) {
            return Axis.X;
         } else {
            if (this.rotationMatrix.m10 != 0) {
               return Axis.Y;
            }

            return Axis.Z;
         }
      case Y:
         if (this.rotationMatrix.m01 != 0) {
            return Axis.X;
         } else {
            if (this.rotationMatrix.m11 != 0) {
               return Axis.Y;
            }

            return Axis.Z;
         }
      case Z:
         if (this.rotationMatrix.m02 != 0) {
            return Axis.X;
         } else {
            if (this.rotationMatrix.m12 != 0) {
               return Axis.Y;
            }

            return Axis.Z;
         }
      default:
         return axis;
      }
   }

   public boolean negativeX() {
      return this.rotationMatrix.m00 != 0 ? this.rotationMatrix.m00 < 0 : (this.rotationMatrix.m01 != 0 ? this.rotationMatrix.m01 < 0 : this.rotationMatrix.m02 < 0);
   }

   public <T> T getX(T x, T y, T z) {
      return (T)(this.rotationMatrix.m00 != 0 ? x : (this.rotationMatrix.m01 != 0 ? y : z));
   }

   public boolean negativeY() {
      return this.rotationMatrix.m10 != 0 ? this.rotationMatrix.m10 < 0 : (this.rotationMatrix.m11 != 0 ? this.rotationMatrix.m11 < 0 : this.rotationMatrix.m12 < 0);
   }

   public <T> T getY(T x, T y, T z) {
      return (T)(this.rotationMatrix.m10 != 0 ? x : (this.rotationMatrix.m11 != 0 ? y : z));
   }

   public boolean negativeZ() {
      return this.rotationMatrix.m20 != 0 ? this.rotationMatrix.m20 < 0 : (this.rotationMatrix.m21 != 0 ? this.rotationMatrix.m21 < 0 : this.rotationMatrix.m22 < 0);
   }

   public <T> T getZ(T x, T y, T z) {
      return (T)(this.rotationMatrix.m20 != 0 ? x : (this.rotationMatrix.m21 != 0 ? y : z));
   }

   public Vector3d getVec() {
      return new Vector3d(this.vec);
   }

   public Rotation.RotationMatrix getMatrix() {
      return this.rotationMatrix;
   }

   public abstract Rotation getOpposite();

   public static class RotationMatrix {
      public int m00;
      public int m01;
      public int m02;
      public int m10;
      public int m11;
      public int m12;
      public int m20;
      public int m21;
      public int m22;

      public RotationMatrix(int m00, int m01, int m02, int m10, int m11, int m12, int m20, int m21, int m22) {
         this.m00 = m00;
         this.m01 = m01;
         this.m02 = m02;
         this.m10 = m10;
         this.m11 = m11;
         this.m12 = m12;
         this.m20 = m20;
         this.m21 = m21;
         this.m22 = m22;
      }

      public RotationMatrix(Rotation.RotationMatrix m1, Rotation.RotationMatrix m2) {
         this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
         this.m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
         this.m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;
         this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
         this.m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
         this.m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;
         this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
         this.m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
         this.m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;
      }

      public int getX(int[] vec) {
         return this.getX(vec[0], vec[1], vec[2]);
      }

      public int getX(Vec3i vec) {
         return this.getX(vec.getX(), vec.getY(), vec.getZ());
      }

      public int getX(int x, int y, int z) {
         return x * this.m00 + y * this.m01 + z * this.m02;
      }

      public long getX(long x, long y, long z) {
         return x * (long)this.m00 + y * (long)this.m01 + z * (long)this.m02;
      }

      public int getY(int[] vec) {
         return this.getY(vec[0], vec[1], vec[2]);
      }

      public int getY(Vec3i vec) {
         return this.getY(vec.getX(), vec.getY(), vec.getZ());
      }

      public int getY(int x, int y, int z) {
         return x * this.m10 + y * this.m11 + z * this.m12;
      }

      public long getY(long x, long y, long z) {
         return x * (long)this.m10 + y * (long)this.m11 + z * (long)this.m12;
      }

      public int getZ(int[] vec) {
         return this.getZ(vec[0], vec[1], vec[2]);
      }

      public int getZ(Vec3i vec) {
         return this.getZ(vec.getX(), vec.getY(), vec.getZ());
      }

      public int getZ(int x, int y, int z) {
         return x * this.m20 + y * this.m21 + z * this.m22;
      }

      public long getZ(long x, long y, long z) {
         return x * (long)this.m20 + y * (long)this.m21 + z * (long)this.m22;
      }

      public BlockPos transform(BlockPos vec) {
         int x = vec.getX() * this.m00 + vec.getY() * this.m01 + vec.getZ() * this.m02;
         int y = vec.getX() * this.m10 + vec.getY() * this.m11 + vec.getZ() * this.m12;
         int z = vec.getX() * this.m20 + vec.getY() * this.m21 + vec.getZ() * this.m22;
         return new BlockPos(x, y, z);
      }

      public Vec3i transform(Vec3i vec) {
         int x = vec.getX() * this.m00 + vec.getY() * this.m01 + vec.getZ() * this.m02;
         int y = vec.getX() * this.m10 + vec.getY() * this.m11 + vec.getZ() * this.m12;
         int z = vec.getX() * this.m20 + vec.getY() * this.m21 + vec.getZ() * this.m22;
         return new Vec3i(x, y, z);
      }

      public void transform(Tuple3f triple) {
         float x = triple.x * (float)this.m00 + triple.y * (float)this.m01 + triple.z * (float)this.m02;
         float y = triple.x * (float)this.m10 + triple.y * (float)this.m11 + triple.z * (float)this.m12;
         float z = triple.x * (float)this.m20 + triple.y * (float)this.m21 + triple.z * (float)this.m22;
         triple.set(x, y, z);
      }

      public void transform(Tuple3d triple) {
         double x = triple.x * (double)this.m00 + triple.y * (double)this.m01 + triple.z * (double)this.m02;
         double y = triple.x * (double)this.m10 + triple.y * (double)this.m11 + triple.z * (double)this.m12;
         double z = triple.x * (double)this.m20 + triple.y * (double)this.m21 + triple.z * (double)this.m22;
         triple.set(x, y, z);
      }

      public boolean equals(Object object) {
         if (!(object instanceof Rotation.RotationMatrix)) {
            return false;
         } else {
            Rotation.RotationMatrix m1 = (Rotation.RotationMatrix)object;
            return this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02 && this.m10 == m1.m10 && this.m11 == m1.m11 && this.m12 == m1.m12 && this.m20 == m1.m20 && this.m21 == m1.m21 && this.m22 == m1.m22;
         }
      }

      public String toString() {
         return this.m00 + ", " + this.m01 + ", " + this.m02 + "\n" + this.m10 + ", " + this.m11 + ", " + this.m12 + "\n" + this.m20 + ", " + this.m21 + ", " + this.m22 + "\n";
      }
   }
}
