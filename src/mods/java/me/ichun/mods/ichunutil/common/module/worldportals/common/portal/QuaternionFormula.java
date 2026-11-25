package me.ichun.mods.ichunutil.common.module.worldportals.common.portal;

import java.util.HashMap;
import javax.vecmath.Matrix3f;
import net.minecraft.util.EnumFacing;

public class QuaternionFormula {
   public static final Quaternion NO_ROTATION_QUAT = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
   public static final QuaternionFormula NO_ROTATION = new QuaternionFormula(NO_ROTATION_QUAT, NO_ROTATION_QUAT);
   public static final float[] NO_ROTATION_FLOAT = new float[]{0.0F, 0.0F, 0.0F};
   public static final HashMap<Float, Quaternion> horizontalQuats = new HashMap();
   public static final HashMap<Float, Quaternion> verticalQuats = new HashMap();
   public Quaternion qHI;
   public Quaternion conjQHI;
   public Quaternion qHO;
   public Quaternion conjQHO;
   public Quaternion qVI;
   public Quaternion conjQVI;
   public Quaternion qVO;
   public Quaternion conjQVO;
   public boolean noRotation;

   public static Quaternion getHorizontalQuaternion(EnumFacing in) {
      float horiAngle = in.getHorizontalAngle() - 0.0125F;
      Quaternion quat = (Quaternion)horizontalQuats.get(Float.valueOf(horiAngle));
      if (quat == null) {
         quat = createQuaternionFromZXYEuler(0.0F, horiAngle, 0.0F);
         horizontalQuats.put(Float.valueOf(horiAngle), quat);
      }

      return quat;
   }

   public static Quaternion getVerticalQuaternion(EnumFacing in) {
      float vertAngle = (float)in.getFrontOffsetY() * 90.0F * 0.99F;
      Quaternion quat = (Quaternion)verticalQuats.get(Float.valueOf(vertAngle));
      if (quat == null) {
         quat = createQuaternionFromZXYEuler(vertAngle, 0.0F, 0.0F);
         verticalQuats.put(Float.valueOf(vertAngle), quat);
      }

      return quat;
   }

   public QuaternionFormula(Quaternion inH, Quaternion outH) {
      this.qHI = inH;
      this.conjQHI = this.qHI.conjugate();
      this.qHO = outH;
      this.conjQHO = this.qHO.conjugate();
      this.qVI = this.qVO = NO_ROTATION_QUAT;
      this.conjQVI = this.conjQVO = NO_ROTATION_QUAT.conjugate();
      this.noRotation = quaternionsIdentical(this.qHI, this.qHO);
   }

   public QuaternionFormula(Quaternion inH, Quaternion outH, Quaternion inV, Quaternion outV) {
      this(inH, outH);
      this.qVI = inV;
      this.conjQVI = this.qVI.conjugate();
      this.qVO = outV;
      this.conjQVO = this.qVO.conjugate();
      this.noRotation = quaternionsIdentical(this.qVI, this.qVO) && this.noRotation;
   }

   public static QuaternionFormula createFromFaces(EnumFacing in, EnumFacing inUp, EnumFacing out, EnumFacing outUp) {
      return !in.getAxis().isVertical() && !out.getAxis().isVertical() ? new QuaternionFormula(getHorizontalQuaternion(in), getHorizontalQuaternion(out)) : new QuaternionFormula(getHorizontalQuaternion(in.getAxis().isVertical() ? inUp : in), getHorizontalQuaternion(out.getAxis().isVertical() ? outUp : out), getVerticalQuaternion(in), getVerticalQuaternion(out));
   }

   public static QuaternionFormula createFromPlanes(EnumFacing in, EnumFacing inUp, EnumFacing out, EnumFacing outUp) {
      return createFromFaces(in.getOpposite(), in == EnumFacing.UP ? inUp : inUp.getOpposite(), out, out == EnumFacing.DOWN ? outUp : outUp.getOpposite());
   }

   public float[] applyPositionalRotation(float[] ori) {
      if (this.noRotation) {
         return ori;
      } else {
         float[] applied = new float[3];
         Quaternion qPos = new Quaternion(ori[0], ori[1], ori[2], 0.0F);
         qPos = this.qHI.mul(qPos).mul(this.conjQHI);
         qPos = this.qVI.mul(qPos).mul(this.conjQVI);
         qPos = this.conjQVO.mul(qPos).mul(this.qVO);
         qPos = this.conjQHO.mul(qPos).mul(this.qHO);
         applied[0] = qPos.x;
         applied[1] = qPos.y;
         applied[2] = qPos.z;
         return applied;
      }
   }

   public float[] applyRotationalRotation(float[] ori) {
      if (this.noRotation) {
         return NO_ROTATION_FLOAT;
      } else {
         Quaternion qRot = createQuaternionFromZXYEuler(ori[1], ori[0], ori[2]);
         qRot = qRot.mul(this.conjQHI);
         if (!quaternionsIdentical(this.qVI, NO_ROTATION_QUAT)) {
            qRot = qRot.mul(this.qVI);
         }

         if (!quaternionsIdentical(this.qVO, NO_ROTATION_QUAT)) {
            qRot = qRot.mul(this.conjQVO);
         }

         qRot = qRot.mul(this.qHO);
         float[] angles = createZXYEulerFromQuaternion(qRot);
         return new float[]{angles[1] - ori[0], angles[0] - ori[1], angles[2] - ori[2]};
      }
   }

   public static Quaternion createQuaternionFromZXYEuler(float x, float y, float z) {
      double radX = Math.toRadians((double)x);
      double radY = Math.toRadians((double)y);
      double radZ = Math.toRadians((double)z);
      float cx = (float)Math.cos(radX);
      float sx = (float)Math.sin(radX);
      float cy = (float)Math.cos(radY);
      float sy = (float)Math.sin(radY);
      float cz = (float)Math.cos(radZ);
      float sz = (float)Math.sin(radZ);
      return (new Quaternion()).setFromMatrix(new Matrix3f(new float[]{cy * cz - sx * sy * sz, -cx * sz, cz * sy + cy * sx * sz, cz * sx * sy + cy * sz, cx * cz, -cy * cz * sx + sy * sz, -cx * sy, sx, cx * cy}));
   }

   public static float[] createZXYEulerFromQuaternion(Quaternion q) {
      float xx = q.x * q.x;
      float xy = q.x * q.y;
      float xz = q.x * q.z;
      float xw = q.x * q.w;
      float yy = q.y * q.y;
      float yz = q.y * q.z;
      float yw = q.y * q.w;
      float zz = q.z * q.z;
      float zw = q.z * q.w;
      Matrix3f matrix = new Matrix3f(new float[]{1.0F - 2.0F * (yy + zz), 2.0F * (xy - zw), 2.0F * (xz + yw), 2.0F * (xy + zw), 1.0F - 2.0F * (xx + zz), 2.0F * (yz - xw), 2.0F * (xz - yw), 2.0F * (yz + xw), 1.0F - 2.0F * (xx + yy)});
      double thetaX;
      double thetaY;
      double thetaZ;
      if (matrix.m21 < 1.0F) {
         if (matrix.m21 > -1.0F) {
            thetaX = Math.asin((double)matrix.m21);
            thetaZ = Math.atan2((double)(-matrix.m01), (double)matrix.m11);
            thetaY = Math.atan2((double)(-matrix.m20), (double)matrix.m22);
         } else {
            thetaX = -1.5707963267948966D;
            thetaZ = -Math.atan2((double)matrix.m02, (double)matrix.m00);
            thetaY = 0.0D;
         }
      } else {
         thetaX = 1.5707963267948966D;
         thetaZ = Math.atan2((double)matrix.m02, (double)matrix.m00);
         thetaY = 0.0D;
      }

      float[] angles = new float[3];
      angles[0] = (float)Math.toDegrees(thetaX);
      angles[1] = (float)Math.toDegrees(thetaY);
      angles[2] = (float)Math.toDegrees(thetaZ);
      return angles;
   }

   public static boolean quaternionsIdentical(Quaternion a, Quaternion b) {
      return a.x == b.x && a.y == b.y && a.z == b.z && a.w == b.w;
   }
}
