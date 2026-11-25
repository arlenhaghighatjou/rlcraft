package com.creativemd.creativecore.common.utils.math.collision;

import com.creativemd.creativecore.common.utils.math.BooleanUtils;
import com.creativemd.creativecore.common.utils.math.box.BoxUtils;
import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;
import java.util.HashMap;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;

public class CollisionCoordinator {
   private HashMap<Double, Matrix4d> table = new HashMap();
   private HashMap<Double, Matrix4d> invertedTable = new HashMap();
   private final double rotXRadians;
   private final double rotYRadians;
   private final double rotZRadians;
   public final double rotX;
   public final double rotY;
   public final double rotZ;
   public final double offX;
   public final double offY;
   public final double offZ;
   public Matrix3d rotationX;
   public Matrix3d rotationY;
   public Matrix3d rotationZ;
   public Vector3d translation;
   public final boolean hasRotX;
   public final boolean hasRotY;
   public final boolean hasRotZ;
   public final boolean hasOffX;
   public final boolean hasOffY;
   public final boolean hasOffZ;
   public final boolean hasTranslation;
   public final boolean hasOneRotation;
   public final boolean hasRotation;
   public final boolean isSimple;
   private final Vector3d rotationCenter;
   public final IVecOrigin moving;
   public IVecOrigin origin;
   private final double originalOffX;
   private final double originalOffY;
   private final double originalOffZ;
   private final double originalRotX;
   private final double originalRotY;
   private final double originalRotZ;

   public CollisionCoordinator(double offX, double offY, double offZ, double rotX, double rotY, double rotZ, IVecOrigin moving, IVecOrigin origin) {
      this.offX = offX;
      this.offY = offY;
      this.offZ = offZ;
      this.rotX = rotX;
      this.rotY = rotY;
      this.rotZ = rotZ;
      this.rotXRadians = Math.toRadians(rotX);
      this.rotYRadians = Math.toRadians(rotY);
      this.rotZRadians = Math.toRadians(rotZ);
      this.hasRotX = rotX != 0.0D;
      this.hasRotY = rotY != 0.0D;
      this.hasRotZ = rotZ != 0.0D;
      this.hasOffX = offX != 0.0D;
      this.hasOffY = offY != 0.0D;
      this.hasOffZ = offZ != 0.0D;
      this.hasTranslation = this.hasOffX || this.hasOffY || this.hasOffZ;
      this.hasOneRotation = BooleanUtils.oneTrue(this.hasRotX, this.hasRotY, this.hasRotZ);
      this.hasRotation = this.hasRotX || this.hasRotY || this.hasRotZ;
      this.isSimple = this.isSimple();
      this.moving = moving;
      this.rotationCenter = new Vector3d(moving.center());
      if (moving.getParent() != null) {
         moving.getParent().transformPointToWorld(this.rotationCenter);
      }

      this.origin = origin;
      this.rotationX = this.hasRotX ? MatrixUtils.createRotationMatrixX(rotX) : null;
      this.rotationY = this.hasRotY ? MatrixUtils.createRotationMatrixY(rotY) : null;
      this.rotationZ = this.hasRotZ ? MatrixUtils.createRotationMatrixZ(rotZ) : null;
      this.translation = this.hasTranslation ? new Vector3d(offX, offY, offZ) : null;
      this.originalOffX = moving.offX();
      this.originalOffY = moving.offY();
      this.originalOffZ = moving.offZ();
      this.originalRotX = moving.rotX();
      this.originalRotY = moving.rotY();
      this.originalRotZ = moving.rotZ();
   }

   public void reset(IVecOrigin origin) {
      this.origin = origin;
      this.moving.off(this.originalOffX, this.originalOffY, this.originalOffZ);
      this.moving.rot(this.originalRotX, this.originalRotY, this.originalRotZ);
   }

   public void move() {
      this.moving.off(this.originalOffX + this.offX, this.originalOffY + this.offY, this.originalOffZ + this.offZ);
      this.moving.rot(this.originalRotX + this.rotX, this.originalRotY + this.rotY, this.originalRotZ + this.rotZ);
   }

   public boolean hasOnlyTranslation() {
      return this.hasTranslation && !this.hasRotX && !this.hasRotY && !this.hasRotZ;
   }

   public double getRotationDegree(Axis axis) {
      switch(axis) {
      case X:
         return this.rotX;
      case Y:
         return this.rotY;
      case Z:
         return this.rotZ;
      default:
         return 0.0D;
      }
   }

   public Matrix3d getRotationMatrix(Axis axis) {
      switch(axis) {
      case X:
         return this.rotationX;
      case Y:
         return this.rotationY;
      case Z:
         return this.rotationZ;
      default:
         return null;
      }
   }

   public AxisAlignedBB computeSurroundingBox(AxisAlignedBB box) {
      return BoxUtils.getRotatedSurrounding(box, this);
   }

   private boolean isSimple() {
      if (BooleanUtils.oneTrue(this.hasRotX, this.hasRotY, this.hasRotZ, this.hasTranslation)) {
         if (this.hasRotX) {
            return Math.abs(this.rotX) <= 180.0D;
         } else if (this.hasRotY) {
            return Math.abs(this.rotY) <= 180.0D;
         } else if (this.hasRotZ) {
            return Math.abs(this.rotZ) <= 180.0D;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public int getNumberOfHalfRotations() {
      int halfRotations = 0;
      if (this.hasRotX) {
         halfRotations += (int)Math.ceil(Math.abs(this.rotX) / 180.0D);
      }

      if (this.hasRotY) {
         halfRotations += (int)Math.ceil(Math.abs(this.rotY) / 180.0D);
      }

      if (this.hasRotZ) {
         halfRotations += (int)Math.ceil(Math.abs(this.rotZ) / 180.0D);
      }

      return halfRotations;
   }

   public Matrix4d getInverted(Double delta) {
      Matrix4d matrix = (Matrix4d)this.invertedTable.get(delta);
      if (matrix != null) {
         return matrix;
      } else {
         matrix = (Matrix4d)this.table.get(delta);
         if (matrix != null) {
            matrix = new Matrix4d(matrix);
            matrix.invert();
         } else {
            matrix = MatrixUtils.createRotationMatrixAndTranslationRadians(-this.offX * delta.doubleValue(), -this.offY * delta.doubleValue(), -this.offZ * delta.doubleValue(), -this.rotXRadians * delta.doubleValue(), -this.rotYRadians * delta.doubleValue(), -this.rotZRadians * delta.doubleValue());
         }

         this.invertedTable.put(delta, matrix);
         return matrix;
      }
   }

   public Matrix4d get(Double delta) {
      Matrix4d matrix = (Matrix4d)this.table.get(delta);
      if (matrix != null) {
         return matrix;
      } else {
         matrix = (Matrix4d)this.invertedTable.get(delta);
         if (matrix != null) {
            matrix = new Matrix4d(matrix);
            matrix.invert();
         } else {
            matrix = MatrixUtils.createRotationMatrixAndTranslationRadians(this.offX * delta.doubleValue(), this.offY * delta.doubleValue(), this.offZ * delta.doubleValue(), this.rotXRadians * delta.doubleValue(), this.rotYRadians * delta.doubleValue(), this.rotZRadians * delta.doubleValue());
         }

         this.table.put(delta, matrix);
         return matrix;
      }
   }

   public void transform(Vector3d vec, Double delta) {
      if (delta.doubleValue() > 0.0D) {
         vec.sub(this.rotationCenter);
         Matrix4d matrix = this.get(delta);
         double x = matrix.m00 * vec.x + matrix.m01 * vec.y + matrix.m02 * vec.z + matrix.m03;
         double y = matrix.m10 * vec.x + matrix.m11 * vec.y + matrix.m12 * vec.z + matrix.m13;
         vec.z = matrix.m20 * vec.x + matrix.m21 * vec.y + matrix.m22 * vec.z + matrix.m23;
         vec.x = x;
         vec.y = y;
         vec.add(this.rotationCenter);
      }
   }

   public void transformInverted(Vector3d vec, Double delta) {
      if (delta.doubleValue() > 0.0D) {
         vec.sub(this.rotationCenter);
         Matrix4d matrix = this.getInverted(delta);
         double x = matrix.m00 * vec.x + matrix.m01 * vec.y + matrix.m02 * vec.z + matrix.m03;
         double y = matrix.m10 * vec.x + matrix.m11 * vec.y + matrix.m12 * vec.z + matrix.m13;
         vec.z = matrix.m20 * vec.x + matrix.m21 * vec.y + matrix.m22 * vec.z + matrix.m23;
         vec.x = x;
         vec.y = y;
         vec.add(this.rotationCenter);
      }
   }

   public void transform(Matrix4d matrix, Vector3d vec) {
      vec.sub(this.rotationCenter);
      double x = matrix.m00 * vec.x + matrix.m01 * vec.y + matrix.m02 * vec.z + matrix.m03;
      double y = matrix.m10 * vec.x + matrix.m11 * vec.y + matrix.m12 * vec.z + matrix.m13;
      vec.z = matrix.m20 * vec.x + matrix.m21 * vec.y + matrix.m22 * vec.z + matrix.m23;
      vec.x = x;
      vec.y = y;
      vec.add(this.rotationCenter);
   }
}
