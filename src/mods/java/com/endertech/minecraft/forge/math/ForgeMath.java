package com.endertech.minecraft.forge.math;

import com.endertech.common.CommonMath;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class ForgeMath {
   public static final float PI = 3.1415927F;

   public ForgeMath() {
   }

   public static float cos(float degrees) {
      return MathHelper.cos(getRadians(degrees));
   }

   public static float sin(float degrees) {
      return MathHelper.sin(getRadians(degrees));
   }

   public static boolean VectorsAreEqual(Vec3d vec1, Vec3d vec2) {
      if (vec1 != null && vec2 != null) {
         return vec1.x == vec2.x && vec1.y == vec2.y && vec1.z == vec2.z;
      } else {
         return vec1 == vec2;
      }
   }

   public static Vect3d getBBCenter(AxisAlignedBB bb) {
      return bb != null ? Vect3d.from(CommonMath.getAverage(bb.minX, bb.maxX), CommonMath.getAverage(bb.minY, bb.maxY), CommonMath.getAverage(bb.minZ, bb.maxZ)) : Vect3d.ZERO;
   }

   public static float getDegrees(float radians) {
      return radians * 180.0F / 3.1415927F;
   }

   public static float getRadians(float degrees) {
      return degrees * 3.1415927F / 180.0F;
   }

   public static float arcTan(double y, double x) {
      return getDegrees((float)Math.atan2(y, x));
   }

   public static float roundTo(float value, int powerOf10) {
      double pow = Math.pow(10.0D, (double)(-powerOf10));
      double mult = (double)value * pow;
      int round = Math.round((float)mult);
      return (float)((double)round / pow);
   }

   public static boolean isNaN(Vec3d vec) {
      return Double.isNaN(vec.x) || Double.isNaN(vec.y) || Double.isNaN(vec.z);
   }
}
